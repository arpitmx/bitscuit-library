[![](https://jitpack.io/v/arpitmx/bitscuit.svg)](https://jitpack.io/#arpitmx/bitscuit)

# bitscuit
bitscuit updater is an android library which the developers can hook into their project and use it to update their apps in just 3 lines of code.

bitscuit is hosted at  <a href="https://jitpack.io/#arpitmx/bitscuit/1.0.5">Jitpack repository</a>

![Logo](https://github.com/arpitmx/bitscuit/assets/59350776/4b40f173-7f7c-4357-b0a0-43b7a6cb5733)


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
## 🔗 Links
[![portfolio](https://img.shields.io/badge/my_portfolio-000?style=for-the-badge&logo=ko-fi&logoColor=white)](https://katherineoelsner.com/)
[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/)
[![twitter](https://img.shields.io/badge/twitter-1DA1F2?style=for-the-badge&logo=twitter&logoColor=white)](https://twitter.com/)

