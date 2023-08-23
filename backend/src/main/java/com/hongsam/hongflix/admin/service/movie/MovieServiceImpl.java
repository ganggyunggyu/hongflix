package com.hongsam.hongflix.admin.service.movie;

import com.hongsam.hongflix.admin.domain.content.Content;
import com.hongsam.hongflix.admin.domain.content.ContentCreateReqDto;
import com.hongsam.hongflix.admin.domain.movie.Movie;
import com.hongsam.hongflix.admin.domain.movie.MovieCreateReqDto;
import com.hongsam.hongflix.admin.domain.movie.MovieUpdateDto;
import com.hongsam.hongflix.admin.repository.admin.content.ContentRepository;
import com.hongsam.hongflix.admin.repository.admin.movie.MovieRepository;
import com.hongsam.hongflix.admin.service.s3.S3UploaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService{

    private final MovieRepository movieRepository;

    private final ContentRepository contentRepository;

    private final S3UploaderService s3UploaderService;

    @Override
    public boolean save(MovieCreateReqDto movieCreateReqDto, MultipartFile file) throws IOException {
        String url = "";
        url = s3UploaderService.upload(file, "static/content-video");
        Movie movie = new Movie(url,
                movieCreateReqDto.getTitle(),
                movieCreateReqDto.getSubTitle(),
                movieCreateReqDto.getContent(),
                movieCreateReqDto.getGenre()
        );

        movie.setAccessKey(url);
        Movie savedMovie = movieRepository.save(movie);
        if(savedMovie.getId() != null){
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public Content addContentToMovie(Long movieId, ContentCreateReqDto contentCreateReqDto, MultipartFile file) throws IOException {
        String url = "";
        if(file != null) {
            url = s3UploaderService.upload(file, "static/content-video");
        }

        Content content = Content.builder()
                .title(contentCreateReqDto.getTitle())
                .accessUrl(url)
                .explanation(contentCreateReqDto.getExplanation())
                .build();
        content.setMovieId(movieId);
        content.setAccessUrl(url);

        return contentRepository.save(content);
    }


    @Override
    public void update(Long id, MovieUpdateDto movieUpdateDto) {
        movieRepository.update(id, movieUpdateDto);
    }

    @Override
    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }

    @Override
    public List<Movie> findMovies() {
        return movieRepository.findAll();
    }
}
