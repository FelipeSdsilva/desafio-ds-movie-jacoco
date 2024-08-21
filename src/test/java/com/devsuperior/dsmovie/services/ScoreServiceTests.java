package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static com.devsuperior.dsmovie.tests.MovieFactory.createMovieEntity;
import static com.devsuperior.dsmovie.tests.ScoreFactory.createScoreDTO;
import static com.devsuperior.dsmovie.tests.ScoreFactory.createScoreEntity;
import static com.devsuperior.dsmovie.tests.UserFactory.createUserEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {

    @InjectMocks
    private ScoreService service;

    @Mock
    private ScoreRepository scoreRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserService userService;

    private ScoreEntity score;
    private UserEntity user;
    private MovieEntity movie;
    private ScoreDTO scoreDTO;

    @BeforeEach
    void setUp() {

        score = createScoreEntity();
        scoreDTO = createScoreDTO();
        user = createUserEntity();
        movie = createMovieEntity();

        when(movieRepository.findById(scoreDTO.getMovieId())).thenReturn(Optional.of(movie));
        when(scoreRepository.saveAndFlush(any())).thenReturn(score);
        when(movieRepository.save(any())).thenReturn(movie);
    }

    @Test
    public void saveScoreShouldReturnMovieDTO() {

        when(userService.authenticated()).thenReturn(user);

        ScoreEntity score1 = new ScoreEntity();
        score1.setValue(4.0);
        score1.setMovie(movie);
        score1.setUser(user);

        ScoreEntity score2 = new ScoreEntity();
        score2.setValue(3.0);
        score2.setMovie(movie);
        score2.setUser(user);

        movie.getScores().add(score1);
        movie.getScores().add(score2);

        MovieDTO result = service.saveScore(scoreDTO);

        double expectedAvg = (double) Math.round(4.0 + 3.0 + scoreDTO.getScore()) / 3;
        Assertions.assertEquals(expectedAvg, result.getScore());
        Assertions.assertEquals(1, result.getCount());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(movie.getId(), result.getId());
        Assertions.assertEquals(movie.getTitle(), result.getTitle());

        Assertions.assertEquals(movie.getScore(), result.getScore());
        Assertions.assertEquals(movie.getCount(), result.getCount());

    }

    @Test
    public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {

        when(movieRepository.findById(scoreDTO.getMovieId())).thenReturn(Optional.empty());
        when(userService.authenticated()).thenReturn(user);


        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.saveScore(scoreDTO);
        });
    }
}
