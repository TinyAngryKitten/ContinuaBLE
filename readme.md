This project is created with Kotlin Multiplatform, which splits the code in three modules.
The common module contains the platform independent code, which will run on both iOS and Android and contains most of the application code.
The iOS and Android modules contains code that is specific to android or ios.

The kotlin project produces a library that each native app uses to interact with Bluetooth devices.
Interactions with the library happen through the DeviceCentral which requires an implementation of
BleCentralInterface, which interfaces with each platforms Bluetooth libraries.

# Running
## iOS
To start this project open iosApp/iosApp.xcworkspace in xcode and run the app on a physical device.
You will likely also need to sign the project yourself before it can be run.

## Android
Open the project in android studio and run it on a physical device,
make sure Bluetooth and location services are enabled.

# Packages
## ble
The ble package contains the DeviceCentral and IntermediateStorage,
which controls how data is passed between the native Bluetooth libraries and the rest of the application.

## bledata
This package contains data classes which are used to describe information passed to and from the Bluetooth libraries.

## data
The data package contains data classes which hold information about measurements, after parsing.

## gatt
The gatt package contains classes which handle data and operations related to the GATT specification and continua guidelines.
UUIDS, Bluetooth Data formats (GATT Values) etc. The services package contains DSL's for parsing each GATT characteristic.

## util
The util package contains utility functions, for instance, methods for working with bits.