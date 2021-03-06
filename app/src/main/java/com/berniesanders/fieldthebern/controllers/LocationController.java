/*
 * Copyright (c) 2016 - Bernie 2016, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.berniesanders.fieldthebern.controllers;

/**
 *
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import com.berniesanders.fieldthebern.exceptions.AddressUnavailableException;
import com.berniesanders.fieldthebern.exceptions.LocationUnavailableException;
import com.berniesanders.fieldthebern.location.StateConverter;
import com.google.android.gms.maps.model.LatLng;
import dagger.Module;
import dagger.Provides;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import javax.inject.Singleton;
import mortar.Presenter;
import mortar.bundler.BundleService;
import org.apache.commons.lang3.StringUtils;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static mortar.bundler.BundleService.getBundleService;

/**
 * Provides a service to access location
 */
public class LocationController extends Presenter<LocationController.Activity> {

  private final Context context;
  private final LocationManager locationManager;

  public interface Activity {
    AppCompatActivity getActivity();
  }

  // package private
  // only instantiated via the dagger module inner class 'LocationModule' below
  LocationController(Context context, LocationManager locationManager) {
    this.context = context;
    this.locationManager = locationManager;
  }

  ////////////////////////////////// Mortar boilerplate ///////////////////////////////////////
  @Override
  public void onLoad(Bundle savedInstanceState) {
    Timber.v("onLoad()");
    //you can safely call getView() to get the activity here
  }

  @Override
  public void dropView(Activity view) {
    super.dropView(view);
    //after this it is no longer safe to call getView()
  }

  //required by mortar
  @Override
  protected BundleService extractBundleService(Activity activity) {
    return getBundleService(activity.getActivity());
  }

  //used to inject this singleton present onto the Activity
  @Module
  public static class LocationModule {

    @Provides
    @Singleton
    LocationController provideTemplateController(Context context, LocationManager locationManager) {
      return new LocationController(context, locationManager);
    }
  }

  /////////////////////////////////////// Rx ///////////////////////////////////////////

  public Observable<Location> get() {
    return Observable.create(new Observable.OnSubscribe<Location>() {
      @Override
      public void call(Subscriber<? super Location> subscriber) {
        try {
          subscriber.onNext(getLocation());
          subscriber.onCompleted();
        } catch (LocationUnavailableException e) {
          subscriber.onError(e);
        }
      }
    });
  }

  public Observable<String> getAddress() {
    return Observable.create(new Observable.OnSubscribe<String>() {
      @Override
      public void call(Subscriber<? super String> subscriber) {
        try {
          String stateCode = getStateForLocation(getLocation());
          subscriber.onNext(stateCode);
          subscriber.onCompleted();
        } catch (LocationUnavailableException e) {
          subscriber.onError(e);
        }
      }
    });
  }

  public Observable<Address> reverseGeocode(final LatLng latLng) {

    return Observable.create(new Observable.OnSubscribe<Address>() {
      @Override
      public void call(Subscriber<? super Address> subscriber) {
        try {
          Address address = getAddressForLocation(latLng.latitude, latLng.longitude);
          subscriber.onNext(address);
          subscriber.onCompleted();
        } catch (LocationUnavailableException e) {
          subscriber.onError(e);
        }
      }
    });
  }

  //////////////////////////////////// Location ////////////////////////////////////////

  @TargetApi(23)
  private Location getLocation() throws LocationUnavailableException {
    if (Build.VERSION.SDK_INT >= 23 &&
        ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
      // TODO: Consider calling: ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      // onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
      Timber.e("location permission error");
      throw new LocationUnavailableException("location permission error");
    }

    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

    if (location == null) {
      throw new LocationUnavailableException("location was null");
    }

    return location;
  }

  private String getStateForLocation(Location location) throws AddressUnavailableException {
    return StateConverter.getStateCode(getAddressForLocation(location).getAdminArea());
  }

  private Address getAddressForLocation(Location location) throws AddressUnavailableException {
    return getAddressForLocation(location.getLatitude(), location.getLongitude());
  }

  private Address getAddressForLocation(double lat, double lng) throws AddressUnavailableException {
    // Errors could still arise from using the Geocoder (for example, if there is no
    // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
    // simply not have an address for a location. In all these cases, we communicate with the
    // receiver using a resultCode indicating failure. If an address is found, we use a
    // resultCode indicating success.

    // The Geocoder used in this sample. The Geocoder's responses are localized for the given
    // Locale, which represents a specific geographical or linguistic region. Locales are used
    // to alter the presentation of information such as numbers or dates to suit the conventions
    // in the region they describe.
    Geocoder geocoder = new Geocoder(context, Locale.getDefault());

    List<Address> addresses = null;
    Address address = null;
    try {
      // Using getFromLocation() returns an array of Addresses for the area immediately
      // surrounding the given latitude and longitude. The results are a best guess and are
      // not guaranteed to be accurate.
      // In this sample, we get just a single address.
      addresses = geocoder.getFromLocation(lat, lng, 1);
    } catch (IOException | IllegalArgumentException ioException) {
      throw new AddressUnavailableException();
    }

    // Handle case where no address was found.
    if (addresses == null || addresses.size() == 0) {
      throw new AddressUnavailableException();
    } else {
      address = addresses.get(0);

      if (address == null || StringUtils.isBlank(address.getAdminArea())) {
        throw new AddressUnavailableException("address or state code was null/blank");
      }

      return address;
    }
  }

  public boolean isLocationEnabled() {
    int locationMode = 0;
    String locationProviders;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      try {
        locationMode =
            Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
      } catch (Settings.SettingNotFoundException e) {
        Timber.w(e, "isLocationEnabled SettingNotFoundException");
      }

      //We want network location
      //LOCATION_MODE_SENSORS_ONLY doesnt really work, thanks google
      return locationMode != Settings.Secure.LOCATION_MODE_OFF
          && locationMode != Settings.Secure.LOCATION_MODE_SENSORS_ONLY;
    } else {
      locationProviders = Settings.Secure.getString(context.getContentResolver(),
          Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
      return !TextUtils.isEmpty(locationProviders);
    }
  }
}
