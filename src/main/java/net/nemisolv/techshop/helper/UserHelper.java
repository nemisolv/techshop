package com.nemisolv.helper;

import com.nemisolv.payload.user.BadgeParam;
import com.nemisolv.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UserHelper {
    private  final UserRepository userRepo;



    public  String generateUsername(String firstName, String lastName) {

        // Normalize the names to decompose the characters
        String convertedFirstName = normalizeAndRemoveDiacritics(firstName).toLowerCase();
        String convertedLastName = normalizeAndRemoveDiacritics(lastName).toLowerCase();
        String username = convertedFirstName + convertedLastName;
        if(firstName !=null && (lastName == null || lastName.isEmpty())) {
            username = firstName.replace(" ", "");
        }
//        do {
//            username = username + (int)(Math.random() * 1000);
//        }while(userRepo.findByUsername(username) != null);

        while(userRepo.findByAccountName(username).isPresent()) {
            username = username + (int)(Math.random() * 1000);
        }

        return username;
    }
    private String normalizeAndRemoveDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    private static final Map<String, Integer> QUESTION_COUNT_CRITERIA = new HashMap<String, Integer>() {{
        put("BRONZE", 10);
        put("SILVER", 50);
        put("GOLD", 100);
    }};

    private static final Map<String, Integer> ANSWER_COUNT_CRITERIA = new HashMap<String, Integer>() {{
        put("BRONZE", 10);
        put("SILVER", 50);
        put("GOLD", 100);
    }};

    private static final Map<String, Integer> QUESTION_UPVOTES_CRITERIA = new HashMap<String, Integer>() {{
        put("BRONZE", 10);
        put("SILVER", 50);
        put("GOLD", 100);
    }};

    private static final Map<String, Integer> ANSWER_UPVOTES_CRITERIA = new HashMap<String, Integer>() {{
        put("BRONZE", 10);
        put("SILVER", 50);
        put("GOLD", 100);
    }};

    private static final Map<String, Integer> TOTAL_VIEWS_CRITERIA = new HashMap<String, Integer>() {{
        put("BRONZE", 1000);
        put("SILVER", 10000);
        put("GOLD", 100000);
    }};

    private static final Map<String, Map<String, Integer>> BADGE_CRITERIA = new HashMap<String, Map<String, Integer>>() {{
        put("QUESTION_COUNT", QUESTION_COUNT_CRITERIA);
        put("ANSWER_COUNT", ANSWER_COUNT_CRITERIA);
        put("QUESTION_UPVOTES", QUESTION_UPVOTES_CRITERIA);
        put("ANSWER_UPVOTES", ANSWER_UPVOTES_CRITERIA);
        put("TOTAL_VIEWS", TOTAL_VIEWS_CRITERIA);
    }};

    public Map<String, Integer> assignBadges(List<BadgeParam> params) {
        Map<String, Integer> badgeCounts = new HashMap<String, Integer>() {{
            put("BRONZE", 0);
            put("SILVER", 0);
            put("GOLD", 0);
        }};

        for(BadgeParam param: params) {
            String badgeType = param.getType();
            long count = param.getCount();

            Map<String, Integer> criteria = BADGE_CRITERIA.get(badgeType);
            for(Map.Entry<String, Integer> entry: criteria.entrySet()) {
                if(count >= entry.getValue()) {
                    badgeCounts.put(entry.getKey(), badgeCounts.get(entry.getKey()) + 1);
                }
            }
        }
        return badgeCounts;
    }


}
