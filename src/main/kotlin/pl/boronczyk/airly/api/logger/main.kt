package pl.boronczyk.airly.api.logger

import com.google.gson.Gson
import org.joda.time.DateTime
import us.ihmc.airly.AirlyClientBuilder
import us.ihmc.airly.SingleMeasurement
import java.io.File

fun main() {
    val file = File("dailySummary.csv")
    if (!file.exists()) {
        file.createNewFile()
        file.appendText("installationId,fromDateTime,tillDateTime,PM1,PM25,PM10,PRESSURE,HUMIDITY,TEMPERATURE\n")
    }
    readValue(DateTime())
}

val installationIds = listOf(
    2290,
    2953,
    2311,
    2257,
    2286,
    2305,
    2604,
    2275,
    2261,
    2247,
    740,
    1022,
    756,
    6383,
    7557,
    754,
    6930,
    6385,
    2883,
    886,
    2735
)

tailrec fun readValue(date: DateTime) {
    val now = DateTime()
    val api = AirlyClientBuilder("asd123easdsadfbwej23rbwefbwe" /*your API Key*/)
    if (now.dayOfMonth != date.dayOfMonth) {
        installationIds.map { installation ->
            api.build().getMeasurements(installation)
                .subscribe { measurements ->
                    File("dailySummary.csv").appendText(
                        Gson().fromJson(Gson().toJson(measurements), us.ihmc.airly.Measurement::class.java)
                            .history.map { singleMeasurement: SingleMeasurement ->
                            if (singleMeasurement.values.isEmpty()) {
                                listOf(
                                    installation,
                                    singleMeasurement.fromDateTime,
                                    singleMeasurement.tillDateTime,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null
                                ).joinToString(",")
                            } else {
                                listOf(
                                    installation,
                                    singleMeasurement.fromDateTime,
                                    singleMeasurement.tillDateTime,
                                    singleMeasurement.values.single { it.name == "PM1" }.value,
                                    singleMeasurement.values.single { it.name == "PM25" }.value,
                                    singleMeasurement.values.single { it.name == "PM10" }.value,
                                    singleMeasurement.values.single { it.name == "PRESSURE" }.value,
                                    singleMeasurement.values.single { it.name == "HUMIDITY" }.value,
                                    singleMeasurement.values.single { it.name == "TEMPERATURE" }.value
                                ).joinToString(",")
                            }
                        }.joinToString("\n", postfix = "\n")
                    )
                }
                .dispose()
        }
    }
    Thread.sleep(3600000)
    readValue(now)
}