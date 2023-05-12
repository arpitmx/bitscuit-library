# bitscuit 🍪  [![](https://jitpack.io/v/arpitmx/bitscuit.svg)](https://jitpack.io/#arpitmx/bitscuit)

bitscuit updater is an android library which the developers can hook into their project and use it to update their apps in just 3 lines of code.

bitscuit is hosted at  <a href="https://jitpack.io/#arpitmx/bitscuit/1.0.5">Jitpack repository</a>

![Logo](https://github.com/arpitmx/bitscuit/assets/59350776/4b40f173-7f7c-4357-b0a0-43b7a6cb5733)


## Features



- **Easy integration** : bitscuit can be easily integrated into any Android app with just three lines of code, handles permissions, configurations, version comparisions,etc. saving developers valuable time and effort, why recreating the wheel? right.

- **Seamless updates** : bitscuit ensures that app updates happen seamlessly, without interrupting user experience or requiring the user to manually update the app.

- **Error handling** : bitscuit handles errors and edge cases like connection problems gracefully, ensuring that the update process is as smooth and error-free as possible for both developers and users.

## Installation

Installing bistcuit is very simple , you can install bitscuit using github release by downloading the latest jar file  


or use Gradle 

#### Step 1 : Use this in build.gradle(module: project)
```gradle
  allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
#### Step 2 : Use this in build.gradle(module: app)
```gradle
 dependencies {
	    implementation 'com.github.arpitmx:bitscuit:1.0.5'
	}
```

Do not forget to add internet permission in manifest if already not present
```xml
<uses-permission android:name="android.permission.INTERNET" />
```



    
## Sample usage 

```kotlin
 ...

//This data can be fetched from your database 
 val url = "https://example.com/update.apk"
 val latestVersion = "1.0.1"
 val changeLogs = "Change logs..."


// Use the buitscuit builder to create the bitscuit instance 
val bitscuit = Bitscuit.BitscuitBuilder()
                    .scope(context = this, appID = this.packageName)
                    .config(url = updatedURL,version="1.0.1",changeLogs="Change logs..")
                    .build() 
  
       
// Use the listenUpdate() function to start listening for updates 
  bitscuit.listenUpdate()   

 ...                 
                    
```
## License
```
   Copyright (C) 2023 Alok Ranjan

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   
```

## Contributing to bitscuit
All pull requests are welcome, make sure to follow the [contribution guidelines](CONTRIBUTING.md)
when you submit pull request.
## Links
[![portfolio](https://img.shields.io/badge/my_portfolio-000?style=for-the-badge&logo=ko-fi&logoColor=white)](https://github.com/arpitmx/)
[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/alokandro/)
[![twitter](https://img.shields.io/badge/twitter-1DA1F2?style=for-the-badge&logo=twitter&logoColor=white)](https://twitter.com/sudoarmax)

