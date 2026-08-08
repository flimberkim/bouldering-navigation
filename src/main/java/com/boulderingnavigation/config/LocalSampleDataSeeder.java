package com.boulderingnavigation.config;

import com.boulderingnavigation.domain.CompletionVideo;
import com.boulderingnavigation.domain.Mountain;
import com.boulderingnavigation.domain.Problem;
import com.boulderingnavigation.domain.Region;
import com.boulderingnavigation.domain.Rock;
import com.boulderingnavigation.domain.VideoPlatform;
import com.boulderingnavigation.repository.CompletionVideoRepository;
import com.boulderingnavigation.repository.MountainRepository;
import com.boulderingnavigation.repository.ProblemRepository;
import com.boulderingnavigation.repository.RegionRepository;
import com.boulderingnavigation.repository.RockRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Seeds the local H2 database with a large, realistic set of mock data so the
 * frontend and manual QA have something resembling a real Korean bouldering
 * database to work against. Only active on the "local" profile.
 *
 * <p>Region/mountain names are curated real-world Korean bouldering areas.
 * Rock and problem names are drawn from pools of plausible Korean
 * bouldering-guide-style names and combined/shuffled with a fixed-seed
 * {@link Random} so the seeded dataset is varied but reproducible across
 * application restarts.
 */
@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalSampleDataSeeder implements CommandLineRunner {

    private static final long RANDOM_SEED = 20260808L;

    /** Region name -> mountains within that region (2-4 per region). */
    private static final Map<String, String[]> REGION_MOUNTAINS = new LinkedHashMap<>();

    static {
        REGION_MOUNTAINS.put("경기도", new String[] {"수락산", "도봉산", "관악산", "광교산"});
        REGION_MOUNTAINS.put("강원도", new String[] {"설악산", "태백산", "정선 병방치"});
        REGION_MOUNTAINS.put("서울특별시", new String[] {"북한산", "인왕산", "남산", "우이암"});
        REGION_MOUNTAINS.put("제주도", new String[] {"한라산", "산방산", "용머리해안"});
        REGION_MOUNTAINS.put("충청북도", new String[] {"월악산", "속리산", "단양 도담삼봉", "옥순봉"});
    }

    /** Pool of plausible generic Korean bouldering rock names. */
    private static final List<String> ROCK_NAME_POOL = List.of(
            "거북바위", "병풍바위", "코끼리바위", "두꺼비바위", "사자바위", "매바위", "곰바위", "학바위",
            "종바위", "촛대바위", "부처바위", "배바위", "말안장바위", "치마바위", "타워바위", "슬랩바위",
            "오버행바위", "크랙바위", "각시바위", "신선바위", "장군바위", "노적봉바위", "독수리바위",
            "여우바위", "표범바위", "해골바위", "다이아몬드바위", "피라미드바위", "전망대바위", "방패바위");

    /** Pool of plausible Korean bouldering problem names (location + feature combos, plus flavor names). */
    private static final List<String> PROBLEM_NAME_POOL = buildProblemNamePool();

    /** Weighted V-scale grade bag: skewed toward easier grades, like a real crag. */
    private static final List<String> GRADE_BAG = buildGradeBag();

    private static final String TOKEN_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

    private final RegionRepository regionRepository;
    private final MountainRepository mountainRepository;
    private final RockRepository rockRepository;
    private final ProblemRepository problemRepository;
    private final CompletionVideoRepository completionVideoRepository;

    @Override
    public void run(String... args) {
        Random random = new Random(RANDOM_SEED);

        for (Map.Entry<String, String[]> regionEntry : REGION_MOUNTAINS.entrySet()) {
            Region region = regionRepository.save(Region.builder().name(regionEntry.getKey()).build());

            for (String mountainName : regionEntry.getValue()) {
                Mountain mountain = mountainRepository.save(
                        Mountain.builder().name(mountainName).region(region).build());
                seedRocks(random, mountain);
            }
        }
    }

    private void seedRocks(Random random, Mountain mountain) {
        int rockCount = 2 + random.nextInt(4); // 2-5 rocks per mountain
        List<String> rockNames = shuffledCopy(random, ROCK_NAME_POOL);

        for (int i = 0; i < rockCount; i++) {
            Rock rock = rockRepository.save(
                    Rock.builder().name(rockNames.get(i)).mountain(mountain).build());
            seedProblems(random, rock);
        }
    }

    private void seedProblems(Random random, Rock rock) {
        int problemCount = 3 + random.nextInt(6); // 3-8 problems per rock
        List<String> problemNames = shuffledCopy(random, PROBLEM_NAME_POOL);

        for (int i = 0; i < problemCount; i++) {
            String grade = GRADE_BAG.get(random.nextInt(GRADE_BAG.size()));
            Problem problem = problemRepository.save(
                    Problem.builder().name(problemNames.get(i)).grade(grade).rock(rock).build());
            seedVideos(random, problem);
        }
    }

    private void seedVideos(Random random, Problem problem) {
        // ~50% of problems get one video, ~15% get a second one, the rest get none.
        double roll = random.nextDouble();
        if (roll < 0.35) {
            return;
        }
        completionVideoRepository.save(randomVideo(random, problem));
        if (roll >= 0.85) {
            completionVideoRepository.save(randomVideo(random, problem));
        }
    }

    private CompletionVideo randomVideo(Random random, Problem problem) {
        double platformRoll = random.nextDouble();
        VideoPlatform platform;
        String url;
        if (platformRoll < 0.55) {
            platform = VideoPlatform.YOUTUBE;
            url = "https://www.youtube.com/watch?v=" + randomToken(random, 11);
        } else if (platformRoll < 0.85) {
            platform = VideoPlatform.INSTAGRAM;
            url = "https://www.instagram.com/reel/" + randomToken(random, 11) + "/";
        } else {
            platform = VideoPlatform.OTHER;
            url = random.nextBoolean()
                    ? "https://vimeo.com/" + (100000000 + random.nextInt(900000000))
                    : "https://tv.naver.com/v/" + (10000000 + random.nextInt(90000000));
        }
        return CompletionVideo.builder().url(url).platform(platform).problem(problem).build();
    }

    private static List<String> shuffledCopy(Random random, List<String> source) {
        List<String> copy = new ArrayList<>(source);
        Collections.shuffle(copy, random);
        return copy;
    }

    private static String randomToken(Random random, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(TOKEN_CHARS.charAt(random.nextInt(TOKEN_CHARS.length())));
        }
        return sb.toString();
    }

    private static List<String> buildProblemNamePool() {
        String[] locations = {"좌측", "우측", "중앙", "상단", "하단", "정면", "후면"};
        String[] features = {
            "크랙", "페이스", "슬랩", "오버행", "다이노", "크럭스", "라인", "볼더", "무브", "홀드런"
        };

        List<String> pool = new ArrayList<>();
        for (String location : locations) {
            for (String feature : features) {
                pool.add(location + " " + feature);
            }
        }

        pool.addAll(List.of(
                "확관 크랙", "화강암 슬랩", "런지 다이노", "미니 크럭스", "직등 라인", "트래버스 라인",
                "다이렉트 라인", "지붕 크럭스", "손가락 크랙", "주먹 크랙", "포켓 홀드", "핀치 무브",
                "볼륨 슬랩", "코너 크랙", "아레테 라인", "루프 오버행", "실핏줄 크랙", "초승달 라인",
                "까치발 슬랩", "장군봉 다이노"));

        return List.copyOf(pool);
    }

    private static List<String> buildGradeBag() {
        Map<String, Integer> gradeWeights = new LinkedHashMap<>();
        gradeWeights.put("V0", 14);
        gradeWeights.put("V1", 14);
        gradeWeights.put("V2", 12);
        gradeWeights.put("V3", 11);
        gradeWeights.put("V4", 10);
        gradeWeights.put("V5", 8);
        gradeWeights.put("V6", 7);
        gradeWeights.put("V7", 6);
        gradeWeights.put("V8", 5);
        gradeWeights.put("V9", 4);
        gradeWeights.put("V10", 3);

        List<String> bag = new ArrayList<>();
        gradeWeights.forEach((grade, weight) -> {
            for (int i = 0; i < weight; i++) {
                bag.add(grade);
            }
        });
        return List.copyOf(bag);
    }
}
