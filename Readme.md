# Godot Android Location Plugin
Android Location API implementation for Godot Game Engine.

### Dependencies
`com.google.android.gms:play-services-location:17.0.0`

### Install
* Clone and import project to Android Studio
* Run `generatePlugin` task
* Copy files according your build type under the `build/outputs/debug/*` or `build/outputs/release/*`
* Install Android Build Template from Godot
* Paste to `plugins` folder from exported template

### References
__NOTE:__ You need to request `android.permission.ACCESS_COARSE_LOCATION` and `android.permission.ACCESS_FINE_LOCATION` permissions.

```python
# Start to get periodic location updates
startLocationUpdates(interval: Int, maxWaitTime: Int)

# Stop location updates
stopLocationUpdates()

# Get last location
getLastKnowLocation()

# Location updates signal
onLocationUpdates(location_data: Dictionary)
    # Dictionary
    # |-> location_data["longitude"]
    # |-> location_data["latitude"]
    # |-> location_data["accuracy"]
    # |-> location_data["verticalAccuracyMeters"]
    # |-> location_data["altitude"]
    # |-> location_data["speed"]
    # |-> location_data["time"]

# Last Known Location signal
onLastKnownLocation(location_data: Dictionary)
    # Dictionary
    # |-> location_data["longitude"]
    # |-> location_data["latitude"]
    # |-> location_data["accuracy"]
    # |-> location_data["verticalAccuracyMeters"]
    # |-> location_data["altitude"]
    # |-> location_data["speed"]
    # |-> location_data["time"]

# Error Signal
onLocationError(errorCode: Int, message: String)
    # 100 -> ACTIVITY_NOT_FOUND
    # 101 -> LOCATION_UPDATES_NULL
    # 102 -> LAST_KNOWN_LOCATION_NULL
    # 103 -> LOCATION_PERMISSION_MISSING
```

### License
    Copyright 2020 Cagdas Caglak(cagdascaglak@gmail.com)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.