/*
 * Made with love by volunteers
 * Copyright 2015 FeelTheBern.org, BernieSanders.com, Coderly, LostPacketSoftware
 * and the volunteers that wrote this code
 * License: GNU AGPLv3 - https://gnu.org/licenses/agpl.html
 */
package com.berniesanders.fieldthebern.models;

import android.location.Address;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.berniesanders.fieldthebern.location.StateConverter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Not to be confused with android.location.Address
 *
 * Docs:
 *      https://github.com/Bernie-2016/fieldthebern-api/wiki/API-Addresses
 * Format:
 * {
 *     id: <address_id>,
 *     type: 'addresses',
 *     attributes: {
 *          longitude: <longitude>,
 *          latitude: <latitude>,
 *          street_1: <street_1>,
 *          street_2: <street_2>,
 *          city: <city>,
 *          state_code: <state_code>,
 *          zip_code: <zip_code>,
 *          visited_at: <time_and_date_of_the_last_visit_made_to_this_address>,
 *          best_canvass_response: <most_supportive_canvass_response_value_for_this_address>,
 *          last_canvass_response: <last_canvass_response_value_for_this_address>
 *    }
 * }
 *
 * NOTE: 'best_canvass_response' and 'last_canvass_response' respond with
 * 'not_yet_visited' and 'unknown' respectively when the address hasn't been canvassed previously.
 */
public class ApiAddress extends CanvassData {

    public static final String TYPE = "addresses";

    Long id; // should be null when sending a new address to the db
    String type = TYPE;
    Attributes attributes = new Attributes();
    Relationships relationships = new Relationships();//if only life were so easy

    @NonNull
    public static ApiAddress from(@NonNull Address address, @Nullable String apartment) {
        return new ApiAddress()
                .attributes(new Attributes()
                        .street1(address.getAddressLine(0))
                        .street2(apartment)
                        .city(address.getLocality())
                        .state(StateConverter.getStateCode(address.getAdminArea()))
                        .zip(address.getPostalCode())
                        .latitude(address.getLatitude())
                        .longitude(address.getLongitude())
                );
    }

    public static class Relationships {
        @SerializedName("most_supportive_resident")
        Person mostSupportiveResident;
        @SerializedName("last_visited_by")
        User lastVisitedBy;

        public Person mostSupportiveResident() {
            return this.mostSupportiveResident;
        }

        public User lastVisitedBy() {
            return this.lastVisitedBy;
        }

        public Relationships mostSupportiveResident(final Person mostSupportiveResident) {
            this.mostSupportiveResident = mostSupportiveResident;
            return this;
        }

        public Relationships lastVisitedBy(final User lastVisitedBy) {
            this.lastVisitedBy = lastVisitedBy;
            return this;
        }

        public static class People {
            List<Person> data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Relationships that = (Relationships) o;

            if (mostSupportiveResident != null ? !mostSupportiveResident.equals(that.mostSupportiveResident) : that.mostSupportiveResident != null) {
                return false;
            }
            return !(lastVisitedBy != null ? !lastVisitedBy.equals(that.lastVisitedBy) : that.lastVisitedBy != null);

        }

        @Override
        public int hashCode() {
            int result = mostSupportiveResident != null ? mostSupportiveResident.hashCode() : 0;
            result = 31 * result + (lastVisitedBy != null ? lastVisitedBy.hashCode() : 0);
            return result;
        }
    }

    public static class Attributes {
        Double longitude;
        Double latitude;
        @SerializedName("street_1")
        String street1;
        @SerializedName("street_2")
        String street2;
        String city;
        @SerializedName("state_code")
        String state;
        @SerializedName("zip_code")
        String zip;
        @SerializedName("visited_at")
        String visitedAt;
        @SerializedName("best_canvass_response")
        String bestCanvassResponse;
        @SerializedName("last_canvass_response")
        String lastCanvassResponse;


        public Double longitude() {
            return this.longitude;
        }

        public Double latitude() {
            return this.latitude;
        }

        public String street1() {
            return this.street1;
        }

        public String street2() {
            return this.street2;
        }

        public String city() {
            return this.city;
        }

        public String state() {
            return this.state;
        }

        public String zip() {
            return this.zip;
        }

        public String visitedAt() {
            return this.visitedAt;
        }

        public String bestCanvassResponse() {
            return this.bestCanvassResponse;
        }

        @CanvassResponse.Response
        public String lastCanvassResponse() {
            return this.lastCanvassResponse;
        }

        public Attributes longitude(final Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Attributes latitude(final Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Attributes street1(final String street1) {
            this.street1 = street1;
            return this;
        }

        public Attributes street2(final String street2) {
            this.street2 = street2;
            return this;
        }

        public Attributes city(final String city) {
            this.city = city;
            return this;
        }

        public Attributes state(final String state) {
            this.state = state;
            return this;
        }

        public Attributes zip(final String zip) {
            this.zip = zip;
            return this;
        }

        public Attributes visitedAt(final String visitedAt) {
            this.visitedAt = visitedAt;
            return this;
        }

        public Attributes bestCanvassResponse(final String bestCanvassResponse) {
            this.bestCanvassResponse = bestCanvassResponse;
            return this;
        }

        public Attributes lastCanvassResponse(final String lastCanvassResponse) {
            this.lastCanvassResponse = lastCanvassResponse;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Attributes that = (Attributes) o;

            if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null) {
                return false;
            }
            if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null)
                return false;
            if (street1 != null ? !street1.equals(that.street1) : that.street1 != null)
                return false;
            if (street2 != null ? !street2.equals(that.street2) : that.street2 != null)
                return false;
            if (city != null ? !city.equals(that.city) : that.city != null) return false;
            if (state != null ? !state.equals(that.state) : that.state != null) return false;
            if (zip != null ? !zip.equals(that.zip) : that.zip != null) return false;
            if (visitedAt != null ? !visitedAt.equals(that.visitedAt) : that.visitedAt != null) {
                return false;
            }
            if (bestCanvassResponse != null ? !bestCanvassResponse.equals(that.bestCanvassResponse) : that.bestCanvassResponse != null) {
                return false;
            }
            return !(lastCanvassResponse != null ? !lastCanvassResponse.equals(that.lastCanvassResponse) : that.lastCanvassResponse != null);

        }

        @Override
        public int hashCode() {
            int result = longitude != null ? longitude.hashCode() : 0;
            result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
            result = 31 * result + (street1 != null ? street1.hashCode() : 0);
            result = 31 * result + (street2 != null ? street2.hashCode() : 0);
            result = 31 * result + (city != null ? city.hashCode() : 0);
            result = 31 * result + (state != null ? state.hashCode() : 0);
            result = 31 * result + (zip != null ? zip.hashCode() : 0);
            result = 31 * result + (visitedAt != null ? visitedAt.hashCode() : 0);
            result = 31 * result + (bestCanvassResponse != null ? bestCanvassResponse.hashCode() : 0);
            result = 31 * result + (lastCanvassResponse != null ? lastCanvassResponse.hashCode() : 0);
            return result;
        }
    }

    public Long id() {
        return id;
    }

    public ApiAddress id(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public ApiAddress type(String type) {
        this.type = type;
        return this;
    }

    public Attributes attributes() {
        return attributes;
    }

    public ApiAddress attributes(Attributes attributes) {
        this.attributes = attributes;
        return this;
    }

    public Relationships relationships() {
        return this.relationships;
    }

    public ApiAddress relationships(final Relationships relationships) {
        this.relationships = relationships;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiAddress that = (ApiAddress) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (attributes != null ? !attributes.equals(that.attributes) : that.attributes != null) {
            return false;
        }
        return !(relationships != null ? !relationships.equals(that.relationships) : that.relationships != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        result = 31 * result + (relationships != null ? relationships.hashCode() : 0);
        return result;
    }
}
