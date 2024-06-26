// Copyright 2017 The Nomulus Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package google.registry.testing;

/**
 * Container for values passed to {@link UserServiceExtension} to set the logged-in user for tests.
 */
public record UserInfo(String email, String authDomain, boolean isAdmin, boolean isLoggedIn) {

  /** Creates a new logged-in non-admin user instance. */
  public static UserInfo create(String email) {
    String authDomain = email.substring(email.indexOf('@') + 1);
    return new UserInfo(email, authDomain, false, true);
  }

  /** Creates a new logged-in admin user instance. */
  public static UserInfo createAdmin(String email) {
    String authDomain = email.substring(email.indexOf('@') + 1);
    return new UserInfo(email, authDomain, true, true);
  }

  /** Returns a logged-out user instance. */
  public static UserInfo loggedOut() {
    return new UserInfo("", "", false, false);
  }
}
