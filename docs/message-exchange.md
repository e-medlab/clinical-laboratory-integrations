# The following section defines the messages exchanged in the experimental design of clinical laboratory analyzer pattern and technology validation for ASTM

Note that **S22510300055** in the messages is the information system specimen ID.
This is an example ID.
In the real case, this ID will be read in from the barcode of the sample, inserted in the query by the Konelab device.

## Query sent by the Konelab T60 analyzer towards LIS

```
H|\^&|||1^Analyzer 1^7.0|||||||P||20251103093250
Q|1|^S22510300055^^||^^^ALL^||||||||O
L|1|N
```

## Response to the analyzer containing order information for the specimen - correct test/procedure identifiers

```
H|\^&|||^KoneLink^1.4.134|||||||P||20251103090913
P|1||PID2||RAINER^RANDMAA||20000703|M||||||||||||||||||||||||||
O|1|S22510300055||^^^ALB|R||||||X||||1|||||||||1|Q\O||||||
O|2|S22510300055||^^^sTfR|R||||||X||||1|||||||||1|Q\O||||||
L|1|N
```

## Results from the Konelab T60 analyzer after processing the order - correct test/procedure identifiers received

```
H|\^&|||1^Analyzer 1^7.0|||||||P||20251103100820
P|1|S22510300055|||||||||||||||||||||||||||||||
O|1|S22510300055^0.0^5^2||^^^ALB^0.0|S||||||X||||1|||||||||1|F
R|1|^^^ALB^0.0|25.5|g/l||N||F||Indiko||20251103095426|Analyzer 1
L|1|N
```

and

```
H|\^&|||1^Analyzer 1^7.0|||||||P||20251103100821
P|1|S22510300055|||||||||||||||||||||||||||||||
O|1|S22510300055^0.0^5^2||^^^sTfR^0.0|S||||||X||||1|||||||||1|F
R|1|^^^sTfR^0.0|1.3|mg/l||N||F||Indiko||20251103095426|Analyzer 1
L|1|N
```

## Response to the analyzer containing order information for the specimen - incorrect test/procedure identifiers

```
H|\^&|||^KoneLink^1.4.134|||||||P||20251103090913
P|1||||RAINER^RANDMAA||20000703|M||||||||||||||||||||||||||
O|1|S22510300055||^^^TestTest|R||||||X||||1|||||||||1|Q\O||||||
L|1|N
```

## Results from the Konelab T60 analyzer after processing the order - incorrect test/procedure identifiers received

Note: this expected message out of the device is constructed from documentation, no actual logs on such a situation could be found.

```
H|\^&|||1^Analyzer 1^7.0|||||||P||20251103100820
P|1|S22510300055|||||||||||||||||||||||||||||||
O|1|S22510300055||^^^TestTest^0.0|R||||||C||||1|||||||||1|X
C|1|I|E105|G
L|1|N
```

# The following section defines the messages exchanged in the experimental design of clinical laboratory analyzer pattern and technology validation for HL7 V2

## Test result sent by the Cobas Liat analyzer towards LIS

```
MSH|^~\&|cobas Liat|Roche|Host|Healthcare Provider|20140716195356+0000||ORU^R30^ORU_R30|dab465c5-517c-4ec8-b8fa-be8b35427672|P|2.5||||||UNICODE UTF-8
PID|||Unknown||unknown|||U
ORC|NW
OBR||||Dummy description|||||||O||||||||||||||F|||||||N/A||SERVICE
NTE|||Run=00016;Device=M1-E-10063;Version=3.3.0.4027;Tube=0027E;TubeExp=2064-02-29;TubeLot=AAJ5
OBX||NM|Influenza A (CDFA)||0|0|||||F|||||SERVICE||f8:dc:7a:06:27:0c|20171014075501-0400
NTE|1||empty
OBX||ST|Influenza A (CDFA)||Not Detected||||||F

```

## Acknowledgement response to the analyzer indicating successful processing of the message

```
MSH|^~\&|Host|Healthcare Provider|cobas Liat|Roche|20140716195357+0000||ACK^R33^ACK|E6BDAFD0-E22B485B-B124-A26174D4434D|P|2.5||||||UNICODE UTF-8
MSA|AA|25fdc862-9a41-45b5-a710-7579038fe168
```

## Acknowledgement response to the analyzer indicating unsuccessful processing of the message

```
MSH|^~\&|Host|Healthcare Provider|cobas Liat|Roche|20140716195357+0000||ACK^R33^ACK|E6BDAFD0-E22B485B-B124-A26174D4434D|P|2.5||||||UNICODE UTF-8
MSA|AE|612b7a6b-8190-4c09-9de6-9a5b01228587
ERR|||204|E
```
