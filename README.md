# PasscodeView
[![Build Status](https://travis-ci.org/kevalpatel2106/PasscodeView.svg?branch=master)](https://travis-ci.org/kevalpatel2106/PasscodeView) [ ![Download](https://api.bintray.com/packages/kevalpatel2106/maven/PasscodeView/images/download.svg) ](https://bintray.com/kevalpatel2106/maven/PasscodeView/_latestVersion) [![API](https://img.shields.io/badge/API-16%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=16)

##### PasscodeView is an Android Library to easily and securely authenticate user with PIN code or using the fingerprint scanner.


## Why❓
- Secure authentication is the key factor in many application (e.g. financial applications). Many application uses PIN based authentication. 
- But Android System doesn't provide any easy to set view for PIN based authentication which can tightly integrate and take advantage of fingerprint API introduced in newer versions of android. This limitation led me to work on this project.

- With use of *PasscodeView* you can easily integrate PIN & Fingerprint based authentication in your application. 


## Features:
This library provide easy and secure PIN authentication view, which
- provides access to built in fingerprint based authentication. This handles all the complexities of imtegrating the fingerprint API with your application.
- provide error feedback when PIN entered is wrong.
- is highly customisable. So that you can match it with your application them. It provides you control over,
  - shape of the each key.
  - color of the key and pin indicators.
  - size of the each single key.
  

## How to integrate?
- ### Gradle Dependency:
  - Add below lines to `app/build.gradle` file of your project.
  ```
  dependencies {
      compile 'com.kevalpatel2106:open-weather-wrapper:1.0'
  }
  ```
  

## Demo: 
**Authentication using PIN**

|<font color="green">Success</font>|<font color="red">Fail</font>|
|:---:|:---:|
|![PIN Success](/resource/pin_success.gif)|![PIN Failed](/resource/fingerprint_failed.gif):|

**Authentication using fingerprint**

|<font color="green">Success</font>|<font color="red">Fail</font>|
|:---:|:---:|
|![Fingerprint Success](/resource/fingerprint_success.gif)|![Fingerprint Failed](/resource/pin_failed.gif):|

*Here is the link of the demo application. 👉 [Demo](resource/sample.apk)*


## How to contribute?
* Check out contribution guidelines 👉[CONTRIBUTING.md](https://github.com/kevalpatel2106/PasscodeView/blob/master/CONTRIBUTING.md)


## What's next?
- Create view for pattern based authentication.
- Build more customisation parameters to provide granular control over the theme of the view. 


## Questions?🤔
Hit me on twitter [![Twitter](https://img.shields.io/badge/Twitter-@kevalpatel2106-blue.svg?style=flat)](https://twitter.com/kevalpatel2106)


## License
Copyright 2017 Keval Patel

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

