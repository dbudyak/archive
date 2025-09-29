# Solution for technical assignment from Wolt.

### How to run service
`./gradlew bootRun`

### How to run tests
`./gradlew test`

### Quick request-response example
Example request:
```bash
curl --location --request POST 'http://localhost:8080/restaurant/schedule/format' \
--header 'Content-Type: application/json' \
--data-raw '{
    "monday": [{"type": "close","value": 3600}],
    "tuesday": [{"type": "open","value": 36000},{"type": "close","value": 64800}],
    "wednesday": [],
    "thursday": [{"type": "open","value": 37800},{"type": "close","value": 64800}],
    "friday": [{"type": "open","value": 36000}],
    "saturday": [{"type": "close","value": 3600},{"type": "open","value": 36000}],
    "sunday": [{"type": "close","value": 3600},{"type": "open","value": 43200},{"type": "close","value": 75600},{"type": "open","value": 77400}]
}'
```

Example response:
```text
Monday: Closed
Tuesday: 10 AM - 6 PM
Wednesday: Closed
Thursday: 10:30 AM - 6 PM
Friday: 10 AM - 1 AM
Saturday: 10 AM - 1 AM
Sunday: 12 PM - 9 PM, 9:30 PM - 1 AM
```

### What possibly might be improved

- more explicit differentiation between dto and presentation objects - maybe just more descriptive naming
- additional type constraints for [Converter](src/main/kotlin/com/wolt/openhours/services/ConverterService.kt) and [Printer](src/main/kotlin/com/wolt/openhours/services/PrinterService.kt)
- more yet unknown handled corner cases and covered with tests
- more meaningful response messages for different kinds of invalid user input
- more appropriate response codes
- better deserialization logic in gson or changing json serialisation library to jackson

### Thoughts about JSON schema 🤔

```thoughts
At first I wanted to see just one timestamp for storing day and time data. But posix day and time will point 
to the incorrect day of the week in the calendar so this might be confusing later when trying to process it.
I.e. 86401 seconds is actually Friday, 2 January 1970 00:00:01 after converting it to GMT. 

To avoid this we can use other timestamps from 2001 year - this can help to represent calendar days better.

Or we can use iCal format which seems common for calendars but I don't have experience with it.

If we still prefer to represent day and time separately maybe it can be better to have json schema like this:

[
  {
    "day": "friday",
    "ranges": [
      {
        "open": 3600,
        "close": 36000
      },
      {
        "open": 48000,
        "close": 64000
      }
    ]
  },
  {
    "day": "sunday",
    "ranges": []
  }
]

of even 

[
  {
    "day": "friday",
    "open": 48000,
    "close": 64000
  },
  {
    "day": "sunday",
    "open": 3600,
    "close": 48000
  }
]

Here time ranges for the days are more specified and this might help to reduce possible invalid input data from the user. 
Also looks a bit easier to process.
```

### Note about service launch

```text
I can run ./gradle bootRun from IDE and under wsl2 but don't have working real linux or mac machines to check how it works. 
If you will get some permission issue when starting gradle chmod +x ./gradlew may help with it.
```




