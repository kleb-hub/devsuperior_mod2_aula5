package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService scoreService;
	
	@Mock
	private UserService userService;
	
	@Mock
	private ScoreRepository scoreRepository;
	
	@Mock
	private MovieRepository movieRepository;
		
	private long existingMovieId, nonExistingMovieId;	
	private MovieEntity movie;
	private ScoreEntity oldScore, newScore;
	private UserEntity user;
	private ScoreDTO scoreDTO;
	private double newScoreValue;
	private double expectedFinalScoreValue;
	
	@BeforeEach
	void setUp() throws Exception {
		
		existingMovieId = 1L;
		nonExistingMovieId = 2L;
		newScoreValue = 6.5;
		expectedFinalScoreValue = 5.5;
		
		user = UserFactory.createUserEntity();
				
		oldScore = ScoreFactory.createScoreEntity();
		newScore = ScoreFactory.createScoreEntity();
		newScore.setValue(newScoreValue);
		
		movie = MovieFactory.createMovieEntity();
		movie.getScores().add(oldScore);
		movie.getScores().add(newScore);
					
		Mockito.when(userService.authenticated()).thenReturn(user);
		
		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());
		
		Mockito.when(movieRepository.save(any())).thenReturn(movie);
		
		Mockito.when(scoreRepository.saveAndFlush(any())).thenReturn(newScore);
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		
		scoreDTO = new ScoreDTO(existingMovieId, newScoreValue);
		
		MovieDTO result = scoreService.saveScore(scoreDTO);
				
		Assertions.assertNotNull(result);		
		Assertions.assertEquals(result.getId(), scoreDTO.getMovieId());
		Assertions.assertEquals(result.getScore(), expectedFinalScoreValue);
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
				
		scoreDTO = new ScoreDTO(nonExistingMovieId, newScoreValue);
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			scoreService.saveScore(scoreDTO);
		});
	}
}
