import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    private static SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        dtt(openAndParseData());
    }

    //Execute
    private static void dtt(List<Attendee> attendees) {
        HashMap<String, List<AvailableAttendee>> byCountry = groupByCountryWithAvailableDate(attendees);

        List<Result> results = new ArrayList<>();
        for (String country : byCountry.keySet()) {
            List<AvailableAttendee> availableAttendees = byCountry.get(country);
            Set<String> availableConferenceDates = getAllAvailableConferenceDateForCountry(availableAttendees);
            Result result = getResult(country, availableAttendees, availableConferenceDates);
            if (result != null) {
                results.add(result);
            }
        }
        writeDataToFile(results);
    }

    private static List<Attendee> openAndParseData() {
        Type ATTENDEE_TYPE = new TypeToken<List<Attendee>>() {
        }.getType();
        Gson gson = new Gson();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("TestNew.json");
        JsonReader reader = new JsonReader(new InputStreamReader(is));
        return gson.fromJson(reader, ATTENDEE_TYPE);
    }

    private static HashMap<String, List<AvailableAttendee>> groupByCountryWithAvailableDate(List<Attendee> attendees) {
        HashMap<String, List<AvailableAttendee>> byCountry = new HashMap<>();
        for (Attendee attendee : attendees) {
            AvailableAttendee availableAttendee = new AvailableAttendee(attendee, getAvailableDatesForConference(attendee.getAvailableDates()));
            if (!byCountry.containsKey(attendee.getCountry())) {
                byCountry.put(attendee.getCountry(), new ArrayList<>(Collections.singletonList(availableAttendee)));
            } else {
                byCountry.get(attendee.getCountry()).add(availableAttendee);
            }
        }
        return byCountry;
    }

    private static List<String> getAvailableDatesForConference(List<String> dates) {
        Collections.sort(dates);
        List<String> result = new ArrayList<>();
        if (dates.size() == 0 || dates.size() == 1) return result;
        for (int i = 1; i < dates.size(); i++) {
            try {
                if (Math.abs(getDayOfMonthFromStringDate(dates.get(i - 1)) - getDayOfMonthFromStringDate(dates.get(i))) == 1) {
                    result.add(dates.get(i - 1));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static int getDayOfMonthFromStringDate(String date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sd.parse(date));
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    private static Result getResult(String country, List<AvailableAttendee> availableAttendees, Set<String> availableConferenceDates) {
        int maxCount = 0;
        String perfectDate = null;
        List<String> emails = new ArrayList<>();

        List<String> list = new ArrayList<>(availableConferenceDates);
        list.sort(Collections.reverseOrder());
        Set<String> resultSet = new LinkedHashSet<>(list);

        for (String date : resultSet) {
            int count = 0;
            List<String> tempEmailList = new ArrayList<>();
            for (AvailableAttendee availableAttendee : availableAttendees) {
                if (availableAttendee.getAvailableStartDates().contains(date)) {
                    tempEmailList.add(availableAttendee.getAttendee().getEmail());
                    count++;
                }
            }
            if (count > maxCount) {
                maxCount = count;
                emails = tempEmailList;
                perfectDate = date;
            }
        }

        if (maxCount != 0) {
            return new Result(country, perfectDate, emails);
        }
        return null;
    }

    private static Set<String> getAllAvailableConferenceDateForCountry(List<AvailableAttendee> availableAttendees) {
        Set<String> availableSingleDates = new HashSet<>();
        for (AvailableAttendee availableAttendee : availableAttendees) {
            availableSingleDates.addAll(availableAttendee.getAvailableStartDates());
        }
        return availableSingleDates;
    }

    private static void writeDataToFile(List<Result> results) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream("output.json");
            byte[] strToBytes = gson.toJson(results).getBytes();
            outputStream.write(strToBytes);
            outputStream.close();
            gson.toJson(results);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
