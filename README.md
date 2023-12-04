# bitscuit 🍪  [![](https://jitpack.io/v/arpitmx/bitscuit.svg)](https://jitpack.io/#arpitmx/bitscuit)

bitscuit updater is an android library which the developers can hook into their project and use it to update their apps in just 3 lines of code. bitscuit is hosted at  <a href="https://jitpack.io/#arpitmx/bitscuit/1.0.5">Jitpack repository</a>

![Logo](https://github.com/arpitmx/bitscuit/assets/59350776/4b40f173-7f7c-4357-b0a0-43b7a6cb5733)



## Applications 
bitscuit is suited for those application :
- Apps which aren't hosted on any store like google play store
- Distributing updates apps to testers
- Apps which have more frequent updates cause app stores like playstore takes a lot of time (1-2 day) for verification of each update  
- For updates involving minor upgrades in the app


## Features

<br><img src="https://github.com/arpitmx/bitscuit/assets/59350776/77c7d735-1c1c-40e4-bb77-1f10c8a4c9c2" width="350"><br><br>


- **Easy integration** : bitscuit can be easily integrated into any Android app with just three lines of code, handles permissions, configurations, version comparisions,etc. saving developers valuable time and effort, why recreating the wheel? right.
<br><br>
<p float="left">
<img src="https://github.com/arpitmx/bitscuit/assets/59350776/8c58fc11-fe35-4be0-90cd-cc7d9101ec8a)" width="350">
</p><br><br>


- **Seamless updates** : bitscuit ensures that app updates happen seamlessly, without interrupting user experience or requiring the user to manually update the app.

<br><br>
<img src="https://github.com/arpitmx/bitscuit/assets/59350776/a703cc37-19e0-4e33-9214-bb744fec87cb" width="350"><br><br>


- **Error handling** : bitscuit handles errors and edge cases like connection problems gracefully, ensuring that the update process is as smooth and error-free as possible for both developers and users.



## Sample usage 

```kotlin
 ...

// Use the buitscuit builder to create the bitscuit instance 
val bitscuit = Bitscuit.BitscuitBuilder(this)
                    .config(url = _UPDATE_URL_ ,version=_LATEST_VERSION_ , changeLogs=_CHANGE_LOGS_ )
                    .build() 
  
       
// Use the listenUpdate() function to start listening for updates 
  bitscuit.listenUpdate()   

 ...                 
                    
```



## Installation

Installing bistcuit is very simple , you can install bitscuit using github release by downloading the latest jar file  

### Using Gradle 

#### Step 1 : Use this in build.gradle(module: project)
```gradle
  allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

//For Gradle 7.0 and above add 'maven { url 'https://jitpack.io' }' in settings.gradle file


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
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

### Using Maven

```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```

```xml
<dependency>
 	<groupId>com.github.arpitmx</groupId>
	<artifactId>bitscuit</artifactId>
	<version>1.0.6</version>
</dependency>
```

Do not forget to add internet permission in manifest if already not present
```xml
<uses-permission android:name="android.permission.INTERNET" />
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
All pull requests are welcome, make sure to follow the [contribution guidelines](Contribution.md)
when you submit pull request.
## Links
[![portfolio](https://img.shields.io/badge/my_portfolio-000?style=for-the-badge&logo=ko-fi&logoColor=white)](https://github.com/arpitmx/)
[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/alokandro/)
[![twitter](https://img.shields.io/badge/twitter-1DA1F2?style=for-the-badge&logo=twitter&logoColor=white)](https://twitter.com/sudoarmax)

