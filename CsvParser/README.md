# CSV parser

This example shows how one could implement simple comma separated values reader and parser in Kotlin.
A sample data [European Mammals Red List for 2009](https://data.europa.eu/euodp/en/data/dataset?res_format=CSV)
from EU is being used.

This sample is modified from [JetBrain's CSVParser](https://github.com/JetBrains/kotlin-native/tree/master/samples/csvparser) example.

## Simple Usage
1. Install [Serverless Framework](https://serverless.com/framework/docs/getting-started/)
2. `$ cd serverless`
3. `$ sls deploy` -> Get the POST Url that's output for your API
4. `$ curl -X POST <DEPLOYED URL> -d '{"fileName": "European_Mammals_Red_List_Nov_2009.csv", "column": 1, "count": 4}'`
