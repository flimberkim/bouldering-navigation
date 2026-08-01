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
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalSampleDataSeeder implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final MountainRepository mountainRepository;
    private final RockRepository rockRepository;
    private final ProblemRepository problemRepository;
    private final CompletionVideoRepository completionVideoRepository;

    @Override
    public void run(String... args) {
        Region gyeonggi = regionRepository.save(Region.builder().name("경기도").build());
        Region gangwon = regionRepository.save(Region.builder().name("강원도").build());

        Mountain suraksan = mountainRepository.save(Mountain.builder().name("수락산").region(gyeonggi).build());
        Mountain seoraksan = mountainRepository.save(Mountain.builder().name("설악산").region(gangwon).build());

        Rock chimaRock = rockRepository.save(Rock.builder().name("치마바위").mountain(suraksan).build());
        Rock towerRock = rockRepository.save(Rock.builder().name("타워바위").mountain(suraksan).build());
        Rock ulsanRock = rockRepository.save(Rock.builder().name("울산바위 하단").mountain(seoraksan).build());

        Problem leftCrack = problemRepository.save(Problem.builder().name("좌측 크랙").grade("V4").rock(chimaRock).build());
        Problem centerFace = problemRepository.save(Problem.builder().name("센터 페이스").grade("V6").rock(chimaRock).build());
        problemRepository.save(Problem.builder().name("슬랩 라인").grade("V2").rock(towerRock).build());
        Problem overhang = problemRepository.save(Problem.builder().name("오버행 크럭스").grade("V8").rock(ulsanRock).build());

        completionVideoRepository.save(CompletionVideo.builder()
                .url("https://youtube.com/watch?v=example1")
                .platform(VideoPlatform.YOUTUBE)
                .problem(leftCrack)
                .build());
        completionVideoRepository.save(CompletionVideo.builder()
                .url("https://instagram.com/p/example2")
                .platform(VideoPlatform.INSTAGRAM)
                .problem(centerFace)
                .build());
        completionVideoRepository.save(CompletionVideo.builder()
                .url("https://youtube.com/watch?v=example3")
                .platform(VideoPlatform.YOUTUBE)
                .problem(overhang)
                .build());
    }
}
