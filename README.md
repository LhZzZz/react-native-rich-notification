
# react-native-rich-notification

## Getting started

`$ npm install react-native-rich-notification --save`

### Mostly automatic installation

`$ react-native link react-native-rich-notification`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-rich-notification` and add `RNRichNotification.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNRichNotification.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.quenice.cardview.RNRichNotificationPackage;` to the imports at the top of the file
  - Add `new RNRichNotificationPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-rich-notification'
  	project(':react-native-rich-notification').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-rich-notification/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-rich-notification')
  	```


## Usage
```javascript
import RNRichNotification from 'react-native-rich-notification';

// TODO: What to do with the module?
RNRichNotification;
```
  