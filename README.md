# Parking Lot
A service for determining parking lot car license plate authorization using OCR Space API.

# Overview
The service receives a path to an image, and sends the image as a base64 string to the OCR Space API in a Http POST request.
Then the API's response will be processed, and an adequate message will be shown to the user, whilst inserting an appropriate record
to a relational local database.
The database has 2 tables - Approved: for approved licensed plates, contains 2 fields - timestamp and plate number. Denied - for denied license plates. contains a reason field 
in addition to the 2 other fields that exist in Approved table.

# Usage
Unpack the repository zip. While in the folder, write the next command in cmd(windows):
"java -jar ParkingLot.jar >imagepath<" 
while >imagepath< is a mandatory argument, that describes a path to a jpg\jpeg\png image.
