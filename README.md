# PasscodeView
[![Build Status](https://travis-ci.org/kevalpatel2106/PasscodeView.svg?branch=master)](https://travis-ci.org/kevalpatel2106/PasscodeView) [ ![Download](https://api.bintray.com/packages/kevalpatel2106/maven/PasscodeView/images/download.svg) ](https://bintray.com/kevalpatel2106/maven/PasscodeView/_latestVersion) [![API](https://img.shields.io/badge/API-16%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=16)  <a href="https://www.paypal.me/kevalpatel2106"> <img src="https://img.shields.io/badge/paypal-donate-yellow.svg" /></a>


##### PasscodeView is an Android Library to easily and securely authenticate the user with the PIN code or using the fingerprint scanner.


## Why❓
- Secure authentication is the key factor in many application (e.g. financial applications). Many application uses PIN-based authentication. 
- But Android System doesn't provide any easy to set the view for PIN-based authentication which can tightly integrate and take advantage of fingerprint API introduced in newer versions of android. This limitation led me to work on this project.

- With the use of *PasscodeView* you can easily integrate PIN & Fingerprint based authentication in your application. 


## Features:
This library provide easy and secure PIN authentication view, which
- provides access to built-in fingerprint-based authentication. This handles all the complexities of integrating the fingerprint API with your application.
- provide error feedback when PIN entered is wrong.
- is highly customizable. So that you can match it with your application them. It provides you control over,
  * color and shape of each key. 👉 [Guide](https://github.com/kevalpatel2106/PasscodeView/wiki/Diffrent-Key-Shapes)
  * localized name of each key in pin keyboard. 👉 [Guide](https://github.com/kevalpatel2106/PasscodeView/wiki/Add-localized-key-names)
  * size of the each single key.
  * color and shape of pin indicators.👉 [Guide](https://github.com/kevalpatel2106/PasscodeView/wiki/Indicators)
  * Control over tactile feedback for key press and authentication success/failure events.

## Demo: 
**Authentication using PIN/Fingerprint**

|Success|Fail|Fingerprint Success|Fingerprint Fail|
|:---:|:---:|:---:|:---:|
|![PIN Success](/resource/pin_success.gif)|![PIN Failed](/resource/pin_failed.gif)|![Fingerprint Success](/resource/fingerprint_success.gif)|![Fingerprint Failed](/resource/fingerprint_failed.gif)|

**Localized Texts**

|English|Hindi|
|:---:|:---:|
|![Locale English](/resource/locale_en.png)|![Locale Hindi](/resource/locale_hn.png)|

**Different Key Shape**

|Rectangle|Circle|Square|
|:---:|:---:|:---:|
|![Rect](/resource/rect_key.png)|![Circle](/resource/circle_key.png)|![Square](/resource/square_key.png)|

*Here is the link of the demo application. 👉 [Demo](resource/sample.apk)*

 
## How to use this library?
- ### Gradle Dependency:
  * Add below lines to `app/build.gradle` file of your project.
  ```
  dependencies {
      compile 'com.kevalpatel2106:passcodeview:1.1'
  }
  ```
  * To integrate using maven visit this [page](https://github.com/kevalpatel2106/PasscodeView/wiki/Dependencies).
  
- ### Add `PinView` in your layout file.
  ```java
  <com.kevalpatel.passcodeview.PinView
          android:id="@+id/pin_view"
          android:layout_width="match_parent"
          android:layout_height="match_parent"
          android:layout_below="@id/imageView"
          app:dividerColor="@color/colorPrimaryDark"
          app:fingerprintDefaultText="Scan your finger to unlock application"
          app:fingerprintEnable="true"
          app:fingerprintTextColor="@color/colorAccent"
          app:fingerprintTextSize="@dimen/finger_print_text_size"
          app:titleTextColor="@android:color/white"/>
  ```
  
- ### Set the correct pin code to authenticate the user in your activity/fragment.
  ```java
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //....
    //...
          
    PinView pinView = (PinView) findViewById(R.id.pin_view);
    pinView.setCorrectPin(new int[]{1, 2, 3, 4});
    //...
  }
  ```

- ### Set the shape of the key you want to use. 
  - There are three built in key shapes. You can also generate your own key by extending [`Key`](https://github.com/kevalpatel2106/PasscodeView/blob/master/passcodeview/src/main/java/com/kevalpatel/passcodeview/keys/Key.java) class.
    * Round key
    * Rectangle key
    * Square key
  - Here is the example for the round keys.
  ```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      //....
      //...
            
      PinView pinView = (PinView) findViewById(R.id.pin_view);
      pinView.setCorrectPin(new int[]{1, 2, 3, 4});
    
      //Build the desired key shape and pass the theme parameters.
      //REQUIRED
      pinView.setKey(new RoundKey.Builder(pinView)
              .setKeyPadding(R.dimen.key_padding)
              .setKeyStrokeColorResource(R.color.colorAccent)
              .setKeyStrokeWidth(R.dimen.key_stroke_width)
              .setKeyTextColorResource(R.color.colorAccent)
              .setKeyTextSize(R.dimen.key_text_size)
              .build());
      
      //...
    }
    ```

- ### Set the shape of the pin indicators you want to use. 
  - There are two built in key shapes. 
    * Round indicator
    * Dot indicator
  - Here is the example for the round indicator. You can learn more about other indicators from [here](https://github.com/kevalpatel2106/PasscodeView/wiki/Indicators).
  ```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      //....
      //...
            
      PinView pinView = (PinView) findViewById(R.id.pin_view);
      pinView.setCorrectPin(new int[]{1, 2, 3, 4});
      pinView.setKey(...);
            
      //Build the desired indicator shape and pass the theme attributes.
      //REQUIRED
      pinView.setIndicator(new CircleIndicator.Builder(pinView)
              .setIndicatorRadius(R.dimen.indicator_radius)
              .setIndicatorFilledColorResource(R.color.colorAccent)
              .setIndicatorStrokeColorResource(R.color.colorAccent)
              .setIndicatorStrokeWidth(R.dimen.indicator_stroke_width)
              .build());
      
      //...
    }
    ```

- ### Set key names.
  - Set the texts to display on different keys. This is optional step. If you don't set the key names, by default `PINView` will display English locale digits.
  - If you want to learn more about key name localization visit [here](https://github.com/kevalpatel2106/PasscodeView/wiki/Add-localized-key-names).
  ```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      //....
      //...
            
      PinView pinView = (PinView) findViewById(R.id.pin_view);
      pinView.setCorrectPin(new int[]{1, 2, 3, 4});
      pinView.setKey(...);
      pinView.setIndicator(...);
      
      //Set the name of the keys based on your locale.
      //OPTIONAL. If not passed key names will be displayed based on english locale.
      pinView.setKeyNames(new KeyNamesBuilder()
            .setKeyOne(this, R.string.key_1)
            .setKeyTwo(this, R.string.key_2)
            .setKeyThree(this, R.string.key_3)
            .setKeyFour(this, R.string.key_4)
            .setKeyFive(this, R.string.key_5)
            .setKeySix(this, R.string.key_6)
            .setKeySeven(this, R.string.key_7)
            .setKeyEight(this, R.string.key_8)
            .setKeyNine(this, R.string.key_9)
            .setKeyZero(this, R.string.key_0));

      //...
    }
    ```
  
- ### Set callback listener to get callbacks when user is authenticated or authentication fails.
  ```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      //....
      //...
            
      PinView pinView = (PinView) findViewById(R.id.pin_view);
      pinView.setCorrectPin(new int[]{1, 2, 3, 4});
      pinView.setKey(...);            //REQUIRED
      pinView.setIndicator(...);      //REQUIRED
      pinView.setKeyNames(...)        //OPTIONAL

      pinView.setAuthenticationListener(new AuthenticationListener() {
                 @Override
                 public void onAuthenticationSuccessful() {
                     //User authenticated successfully.
                     //Navigate to next screens.
                 }
     
                 @Override
                 public void onAuthenticationFailed() {
                     //Calls whenever authentication is failed or user is unauthorized.
                     //Do something if you want to handle unauthorized user.
                 }
             });
             
      //...
    }
  ```
  
## [Visit our wiki page for more information.](https://github.com/kevalpatel2106/PasscodeView/wiki)

## How to contribute?
* Check out contribution guidelines 👉[CONTRIBUTING.md](https://github.com/kevalpatel2106/PasscodeView/blob/master/CONTRIBUTING.md)


## What's next?
- Create view for pattern based authentication. (**Upcoming Release** preview)

![Pattern Unlock](/resource/pattern_unlock.gif)

- Build more customisation parameters to provide granular control over the theme of the view. 


## Questions?🤔
Hit me on twitter [![Twitter](https://img.shields.io/badge/Twitter-@kevalpatel2106-blue.svg?style=flat)](https://twitter.com/kevalpatel2106)


## License
Copyright 2017 Keval Patel

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

