package com.base2.kagura.contribute.dynamodb.report.connector;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

/**
 * Created by arran on 5/09/2016.
 */
public class MovieSampleDataObject {
	Integer year;
	String title;

	MovieInfo info;

	public class MovieInfo {
		List<String> directors;
		@JsonProperty("release_date")
		String released;
		Integer rating;
		List<String> genres;
		@JsonProperty("image_url")
		String imageUrl;
		String plot;
		Integer rank;
		@JsonProperty("running_time_secs")
		Integer runningTime;
		List<String> actors;

		public List<String> getDirectors() {
			return directors;
		}

		public void setDirectors(List<String> directors) {
			this.directors = directors;
		}

		public String getReleased() {
			return released;
		}

		public void setReleased(String released) {
			this.released = released;
		}

		public Integer getRating() {
			return rating;
		}

		public void setRating(Integer rating) {
			this.rating = rating;
		}

		public List<String> getGenres() {
			return genres;
		}

		public void setGenres(List<String> genres) {
			this.genres = genres;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public String getPlot() {
			return plot;
		}

		public void setPlot(String plot) {
			this.plot = plot;
		}

		public Integer getRank() {
			return rank;
		}

		public void setRank(Integer rank) {
			this.rank = rank;
		}

		public Integer getRunningTime() {
			return runningTime;
		}

		public void setRunningTime(Integer runningTime) {
			this.runningTime = runningTime;
		}

		public List<String> getActors() {
			return actors;
		}

		public void setActors(List<String> actors) {
			this.actors = actors;
		}
	}


	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public MovieInfo getInfo() {
		return info;
	}

	public void setInfo(MovieInfo info) {
		this.info = info;
	}
}
