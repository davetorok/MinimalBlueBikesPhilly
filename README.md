# Minimal Blue Bikes Philly

## About
Minimal Blue Bikes Philly (Originally  "MinimalIndego") displays station status for the Indego Bike Share in Philadelphia. There is NO MAP, NO GPS, and works on Android 2.3+.


## Why Minimal?

Indego Bike Share in Philadelpha launched in April 2015.  I have a 5 year old phone running Android 2.3 and I wanted a quick and light app to find stations, bikes, and docks.
I know the city fairly well and didn't want to wait for a map to load on my slow phone.  I also keep GPS off to save battery so I didn't want to require GPS to find "nearest" stations.
The only required permission is Internet access to access the station data.

## Concepts, Features, and Getting Started

* **Station List View** The main view displays:
  * the Station Name and Distance from Current Station
  * Direction Arrow - bearing from Current Station
  * Bike Icon.  Darker blue = fewer bikes.  Lighter blue = more bikes.  Red = Empty.  Yellow = Almost Empty.  Grey = Inactive
  * Number of Available Bikes
  * Number of Available Docks (Magenta/Red to indicate almost full / full)
* **Home Station** Long-press a station to select a station as your home station.  Home stations are displayed in **bold** and always display in the "Favorites" view
* **Current Station** Long-press a station to select a station as the "current" station.  Current Station is marked with a red "bullseye" icon.
The Current Station is used as the origin for "Sort by Distance" and "Sort by Direction".  Current Station always displays in the "Favorites" view
* **Favorites** can be selected from the station detail view (tap a station in the list, the select the favorite star).  In the Station List view, select the favorites icon or menu item to toggle between
all stations and favorite stations.
* **Statistics View** Shows the overall system status -- number of available bikes and docks, inactive/active stations, and how many stations are full/empty.
* **Station Detail View**  Select a station in the Station List View.  Displays the address and the location hint.
* **Station Location Hint** Shown in the Station Detail View.
* **Sorting** Select the sort icon or menu item. The station list can be sorted by Distance, Name, Number of Bikes, Number of Docks, and by Direction

* **Distances are in Manhattan (Taxi) Distances**  All distances are in pure rectangular grid distances aligned to the Center City grid (tilted 9.9 degrees from true north).
This will overestimate if there is a diagonal street (Ben Franklin Parkway, etc). and will underestimate if you have to cross a river or are in a weird area like Northern Liberties or Mantua

* **Direction Arrow** is also aligned to the city grid, not the compass.

* **Manual and Auto Refresh** By default, By default, data will "Auto Refresh" every 5 minutes.  Auto Refresh can be turned off or set to 1,2,5,10,30 minutes in the Settings.  Data can be manually refreshed at any time with the menu / action bar item.
* **Stale Data Warnings** By default the last refresh time text will turn Yellow after 3 minutes and Red after 5 minutes.  The Warning time can be turned off or changed to 1,3,5,10 minutes in the Settings

## Helpful Hints
* Use the "Current Station" feature (long press a station)to find stations near your destination, or to find available bikes or docks near a desired station if full
* The Settings screen allows for selecting the default sort type (distance is recommended) and whether the Home or Current station is set at app startup.


## Acknowledgements

* Uses [Android GeoJson by Jonathan Baker](https://github.com/cocoahero/android-geojson)
* Statistics View inspired by [Bike Share Map by Oliver O'Brien](http://oobrien.com/bikesharemap/)
* Bike Station REST API at https://api.phila.gov/bike-share-stations/v1
* [Open Data Philly](https://www.opendataphilly.org/dataset/bike-share-stations)
