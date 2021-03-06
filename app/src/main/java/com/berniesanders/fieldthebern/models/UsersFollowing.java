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

package com.berniesanders.fieldthebern.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/**
 *
 */
public class UsersFollowing implements Parcelable {

  java.util.List<FollowingUser> data;

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeList(this.data);
  }

  public UsersFollowing() {
  }

  protected UsersFollowing(Parcel in) {
    this.data = new ArrayList<>();
    in.readList(this.data, List.class.getClassLoader());
  }

  public static final Parcelable.Creator<UsersFollowing> CREATOR =
      new Parcelable.Creator<UsersFollowing>() {
        public UsersFollowing createFromParcel(Parcel source) {
          return new UsersFollowing(source);
        }

        public UsersFollowing[] newArray(int size) {
          return new UsersFollowing[size];
        }
      };
}
