# PasscodeView
[![Build Status](https://travis-ci.org/kevalpatel2106/PasscodeView.svg?branch=master)](https://travis-ci.org/kevalpatel2106/PasscodeView) [ ![Download](https://api.bintray.com/packages/kevalpatel2106/maven/PasscodeView/images/download.svg) ](https://bintray.com/kevalpatel2106/maven/PasscodeView/_latestVersion) [![API](https://img.shields.io/badge/API-16%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=16)  <a href="https://www.paypal.me/kpatel2106"> <img src="https://img.shields.io/badge/paypal-donate-yellow.svg" /></a> [![Javadoc](https://img.shields.io/badge/JavaDoc-master-brightgreen.svg?style=orange)](http://kevalpatel2106.com/PasscodeView/)


##### PasscodeView is an Android Library to easily and securely authenticate the user with the PIN code or using the fingerprint scanner.


## Why❓
- Secure authentication is the key factor in many application (e.g. financial applications). Many application uses PIN-based authentication. 
- But Android System doesn't provide any easy to set the view for PIN-based authentication which can tightly integrate and take advantage of fingerprint API introduced in newer versions of android. This limitation led me to work on this project.

- With the use of *PasscodeView*, you can easily integrate PIN & Fingerprint based authentication in your application. 


### Features:
 This library provides an easy and secure PIN and Pattern based authentication view, which
 - It provides access to built-in fingerprint-based authentication if the device supports fingerprint hardware. This handles all the complexities of integrating the fingerprint API with your application.
 - It provides error feedback when PIN or pattern entered is wrong.
 - Extremely lightweight.
 - Supports dynamic PIN sizes for PIN-based authentication. That means you don't have to provide a number of PIN digits at runtime. 
 - Supports custom authentication logic for PIN and Pattern. That means you can send the PIN or pattern to the server for authentication too.
 - It is highly customizable. So that you can match it with your application them. It provides you control over,
   * color and shape of each key. 👉 [Guide](https://github.com/kevalpatel2106/PasscodeView/wiki/Diffrent-Key-Shapes)
   * localized name of each key in pin keyboard. 👉 [Guide](https://github.com/kevalpatel2106/PasscodeView/wiki/Add-localized-key-names)
   * size of every single key.
   * color and shape of indicators to display a number of digits in the PIN.👉 [Guide](https://github.com/kevalpatel2106/PasscodeView/wiki/Indicators)
   * color and shape of pattern indicators.
   * tactile feedback for key press and authentication success/failure events.
   
   
## Demo: 
**Authentication using PIN/Fingerprint**

|Success|Fail|
|:---:|:---:|
|![PIN Success](/resource/pin_success.gif)|![PIN Failed](/resource/pin_failed.gif)|

|Fingerprint Success|Fingerprint Fail|
|:---:|:---:|
|![Fingerprint Success](/resource/fingerprint_success.gif)|![Fingerprint Failed](/resource/fingerprint_failed.gif)|

**Pattern based authentication**

![Pattern Unlock](/resource/pattern_unlock.gif)

*Here is the link of the demo application. 👉 [Demo](resource/sample.apk)*


## How to use this library?
- ### Gradle Dependency:
  * Add below lines to `app/build.gradle` file of your project.
  ```
  dependencies {
      compile 'com.kevalpatel2106:passcodeview:2.0.0'
  }
  ```
  * To integrate using maven visit this [page](https://github.com/kevalpatel2106/PasscodeView/wiki/Dependencies).

## PIN based authentication:

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
  
- ### Get the instance of the view in your activity/fragment.
  ```java
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //....          
    PinView pinView = (PinView) findViewById(R.id.pin_view);
    //...
  }
  ```

- ### Set the authenticator which will tell if your pin is correct or not. 
  - The library provides inbuilt `PasscodeViewPinAuthenticator`. This authenticator will match the pin entered by the user with the correct PIN provided.
  - You can write your custom authenticator to customize the authentication logic.
   ```java    
      //Set the authenticator.
      //REQUIRED
      final int[] correctPin = new int[]{1, 2, 3,4}; 
      pinView.setPinAuthenticator(new PasscodeViewPinAuthenticator(correctPin));
  ```

- ### Set the PIN length. 
  - If you know the number of digits in the PIN you can set it. 
  - But if you don't know the size of the PIN in advance you can set it to `PinView.DYNAMIC_PIN_LENGTH`. By default, if you don't set the size `PinView` will consider it dynamic PIN length.
  ```java
    pinView.setPinLength(PinView.DYNAMIC_PIN_LENGTH);
  ```


- ### Set the shape of the key you want to use. 
  - There are three built-in key shapes. You can also generate your own key by extending [`Key`](https://github.com/kevalpatel2106/PasscodeView/blob/master/passcodeview/src/main/java/com/kevalpatel/passcodeview/keys/Key.java) class.
    * Round key
    * Rectangle key
    * Square key
  - You can create your custom key using this guide. 👉 [Custom key wiki](https://github.com/kevalpatel2106/PasscodeView/wiki/Custom-key-shape)
  - Here is the example for the round keys.
   ```java
    //Build the desired key shape and pass the theme parameters.
    //REQUIRED
    pinView.setKey(new RoundKey.Builder(pinView)
            .setKeyPadding(R.dimen.key_padding)
            .setKeyStrokeColorResource(R.color.colorAccent)
            .setKeyStrokeWidth(R.dimen.key_stroke_width)
            .setKeyTextColorResource(R.color.colorAccent)
            .setKeyTextSize(R.dimen.key_text_size));
   ```
  **Different Key Shape**
    
  |Rectangle|Circle|Square|
  |:---:|:---:|:---:|
  |![Rect](/resource/rect_key.png)|![Circle](/resource/circle_key.png)|![Square](/resource/square_key.png)|

- ### Set the shape of the pin indicators you want to use. 
  - There are three built in key shapes.
    * Round indicator
    * Dot indicator
    * Circle indicator
  - If you want to create custom indicator with the custom shape, see [How to create custom indicator?](https://github.com/kevalpatel2106/PasscodeView/wiki/Custom-indicator).
  - Here is the example for the round indicator.
  ```java
      //Build the desired indicator shape and pass the theme attributes.
      //REQUIRED
      pinView.setIndicator(new CircleIndicator.Builder(pinView)
              .setIndicatorRadius(R.dimen.indicator_radius)
              .setIndicatorFilledColorResource(R.color.colorAccent)
              .setIndicatorStrokeColorResource(R.color.colorAccent)
              .setIndicatorStrokeWidth(R.dimen.indicator_stroke_width));
    ```

- ### Set key names.
  - Set the texts to display on different keys. This is an optional step. If you don't set the key names, by default `PINView` will display English locale digits.
  - If you want to learn more about key name localization visit [here](https://github.com/kevalpatel2106/PasscodeView/wiki/Add-localized-key-names).
  ```java
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
    ```
  
  **Localized Texts**

  |English|Hindi|
  |:---:|:---:|
  |![Locale English](/resource/locale_en.png)|![Locale Hindi](/resource/locale_hn.png)|


- ### Set callback listener to get callbacks when the user is authenticated or authentication fails.
  ```java
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
  ```


## Pattern based authentication:

- ### Add `PatternView` in your layout file.
  ```xml
  <com.kevalpatel.passcodeview.PatternView
          android:id="@+id/pattern_view"
          android:layout_width="match_parent"
          android:layout_height="match_parent"
          android:layout_below="@id/imageView"
          app:dividerColor="@color/colorPrimaryDark"
          app:fingerprintDefaultText="Scan your finger to unlock application"
          app:fingerprintEnable="true"
          app:fingerprintTextColor="@color/colorAccent"
          app:fingerprintTextSize="@dimen/finger_print_text_size"
          app:giveTactileFeedback="true"
          app:patternLineColor="@color/colorAccent"
          app:titleTextColor="@android:color/white"/>
  ```


- ### Get the instance of the view in your activity/fragment.
  ```java
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    PatternView patternView = (PatternView) findViewById(R.id.pattern_view);
    //...
  }
  ```

- ### Set the number of rows and columns of the pattern in your activity/fragment.
  ```java
    //Set number of pattern counts.
    //REQUIRED
    patternView.setNoOfColumn(3);   //Number of columns
    patternView.setNoOfRows(3);     //Number of rows
  ```

- ### Set the authenticator
  - Set the authenticator which will tell if your pattern is correct or not. 
  - The library provides inbuilt `PasscodeViewPatternAuthenticator `. This authenticator will match the pattern entered by the user with the correct pattern provided.
  - You can write your custom authenticator to customize the authentication logic.
  - Here is the example with the inbuilt pattern authenticator. Make sure your `PatternPiont` row or column number is not greater than the number of row and number of columns from the previous step.
   ```java    
      //Set the correct pin code.
      //Display row and column number of the pattern point sequence.
      //REQUIRED
      final PatternPoint[] correctPattern = new PatternPoint[]{
              new PatternPoint(0, 0),
              new PatternPoint(1, 0),
              new PatternPoint(2, 0),
              new PatternPoint(2, 1)
      };
      patternView.setAuthenticator(new PasscodeViewPatternAuthenticator(correctPattern));
  ```

- ### Set the pattern cell shape.
  - There are two built-in pattern cells available.
    * Circle indicator
    * Dot indicator
  - If you want to create custom pattern cell with the custom shape, see [How to create custom indicator?](https://github.com/kevalpatel2106/PasscodeView/wiki/Custom-indicator).
  - Here is the example of the round indicator. 
  ```java
    //Build the desired indicator shape and pass the theme attributes.
    //REQUIRED
    patternView.setPatternCell(new CirclePatternCell.Builder(patternView)
            .setRadius(R.dimen.pattern_cell_radius)
            .setCellColorResource(R.color.colorAccent));
  ```

- ### Set callback listener to get callbacks when the user is authenticated or authentication fails.
```java
    patternView.setAuthenticationListener(new AuthenticationListener() {
        @Override
        public void onAuthenticationSuccessful() {
            //User authenticated successfully.
        }

        @Override
        public void onAuthenticationFailed() {
            //Calls whenever authentication is failed or user is unauthorized.
            //Do something
        }
    });
  ```

*[**Visit our wiki page for more information.**](https://github.com/kevalpatel2106/PasscodeView/wiki)*

## How to contribute?
* Check out contribution guidelines 👉[CONTRIBUTING.md](https://github.com/kevalpatel2106/PasscodeView/blob/master/CONTRIBUTING.md)

## Questions?🤔
Hit me on twitter [![Twitter](https://img.shields.io/badge/Twitter-@kevalpatel2106-blue.svg?style=flat)](https://twitter.com/kevalpatel2106)


## License
Copyright 2017 Keval Patel

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

<div align="center">
<img src="https://cloud.githubusercontent.com/assets/370176/26526332/03bb8ac2-432c-11e7-89aa-da3cd1c0e9cb.png">
</div>
