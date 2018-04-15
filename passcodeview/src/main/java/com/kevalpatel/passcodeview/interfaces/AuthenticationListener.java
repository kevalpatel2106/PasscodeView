/*
 * Copyright 2018 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel.passcodeview.interfaces;

/**
 * Created by Keval on 06-Apr-17.
 * Interface to notify you authentication result whenever the authentication process completes.
 *
 *@author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
public interface AuthenticationListener {

    /**
     * This method indicates that authentication is successful (either by matching pin ,matching
     * password or correct fingerprint).
     */
    void onAuthenticationSuccessful();

    /**
     * Authentication failed.
     */
    void onAuthenticationFailed();
}
