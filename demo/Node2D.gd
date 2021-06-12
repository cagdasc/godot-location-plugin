"""
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
"""
extends Node2D

var singleton = null

func _ready():
	get_tree().connect("on_request_permissions_result", self, "result")
	if (OS.request_permissions()):
		print("permissions ok")
		result("", true)

func _exit_tree():
	if singleton != null:
		singleton.stopLocationUpdates()

func on_location_updates(updates_dict: Dictionary):
	print("Location Updates: ", updates_dict)

func on_last_known_location(last_known_location: Dictionary):
	print("Location Updates: ", last_known_location)

func on_location_error(errorCode: int, message: String):
	print("Error Code: ", errorCode, " Message: ", message)

func result(permission: String, granted: bool):
	if Engine.has_singleton("LocationPlugin"):
		singleton = Engine.get_singleton("LocationPlugin")
		singleton.connect("onLocationUpdates", self, "on_location_updates")
		singleton.connect("onLastKnownLocation", self, "on_last_known_location")
		singleton.connect("onLocationError", self, "on_location_error")
		singleton.startLocationUpdates(6000.0, 10000.0)
		singleton.getLastKnowLocation()
