package com.ventulus95.jpastudy.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player {

	@Id
	@Column(name = "Player_ID")
	private Long id;

	private String name;

	@ManyToOne
	@JoinColumn(name = "TEAM_ID")
	private Team team;

	public Player(Long id, String name) {
		this.id = id;
		this.name = name;
	}
}
