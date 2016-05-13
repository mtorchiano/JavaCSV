# JavaCSV
A parsing processor for CSV written in Java

Currently the parser performance is fairly reasonable when compared to `wc` n*x utility, which performs no parsing at all.
 
| Utility | Throughput|
|---------|-----------|
|   wc 		| 238 MB/s  |
|CsvParser | 115 MB/s |

Processing a 1.3GB file on a MacBook Pro (Retina, 15-inch, Late 2013) 2.3 GHz Intel Core i7 
