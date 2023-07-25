# Maximum Futility
by Mark Abersold

## What is this project?
This is the result of a question I asked myself one day: "What is the most hopeless sports town in North America?" I decided to gather the data together and calculate it.

### How to build and run it
This is built in Kotlin using the KTor framework. You can build it and run it using gradle.

    gradle build
    gradle run

Or, if you don't have gradle installed, using the handy gradlew files that are included.

    gradlew build
    gradlew run

Once built, direct your browser to http://localhost:8080 to see the output.

### Database Schema

Run with the command line argument `--createSchema` to create the database schema. This will create a SQLite database file called `maximum_futility.db` in the build directory.

    gradle run --args="--createSchema"

## How it Works

The data is stored as JSON files. Although the main output is by city, the data is stored by franchise. When you load the main page, it maps the franchise data to city data. It also performs all the necessary calculations to give you the output.

### Where is the data?
Look under src/main/resources/data for the JSON files that contain all the franchise data.

### What if I see a mistake in your data?
Please file an issue to correct it! Or, if you're feeling confident, you can make the change yourself and open a pull request.

### Why are you using JSON files and not a database, like SQLite?
For now, JSON files are sufficient. I may eventually use a database instead, depending on how complex the operations become.