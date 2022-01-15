package com.ventulus95.jpastudy;

import static org.assertj.core.api.BDDAssertions.assertThat;
import static org.assertj.core.api.BDDAssertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ventulus95.jpastudy.domain.Player;
import com.ventulus95.jpastudy.domain.Team;

@DataJpaTest
public class ManyToOneUniDirectTest {

	@PersistenceContext
	EntityManager em;

	@BeforeEach
	void setting(){
		Team team1 = new Team(1L, "팀1");
		em.persist(team1);
		Player player1 = new Player(1L, "맴버1");
		player1.setTeam(team1);
		em.persist(player1);
		Player player2 = new Player(2L, "맴버2");
		player2.setTeam(team1);
		em.persist(player2);
		em.flush();
	}

	@DisplayName("회원과 팀을 저장하는 테스트")
	@Test
	void saveTest(){
		Player findPlayer = em.find(Player.class, 1L);
		assertThat(findPlayer.getId(), is(1L));
		assertThat(findPlayer.getTeam().getName(), is("팀1"));
	}

	@DisplayName("회원과 팀을 수정하는 테스트")
	@Test
	void updateTest(){
		Team team2 = new Team(2L, "팀2");
		em.persist(team2);
		Player findPlayer = em.find(Player.class, 2L);
		findPlayer.setTeam(team2);

		em.flush(); // 영속화되어있는 상태라서 플러시만 했는데도
		// Hibernate: update player set name=?, team_id=? where player_id=? 업데이트 쿼리가 자동으로 발송된다. D
		// irty check를 통해서 자동으로 Update 쿼리를 자동 발송해준것.

		assertThat(findPlayer.getTeam(), is(team2));
		assertThat(findPlayer.getTeam().getId(), is(team2.getId()));
	}

	@DisplayName("회원과 팀을 삭제하는 테스트")
	@Test
	void deleteTeamTest(){
		Player findPlayer = em.find(Player.class, 2L);
		findPlayer.setTeam(null);
		em.flush();
		assertThat(findPlayer.getTeam(), nullValue());
	}

	@DisplayName("팀에 맴버가 있는데 팀을 삭제하려는 에러가 영속화에서만 삭제가되서 에러가 발생하지 않는 테스트")
	@Test
	void deleteRelateTeamNotErrorTest(){
		Team findTeam = em.find(Team.class, 1L);
		Throwable thrown = catchThrowable(()->{
			em.remove(findTeam);//flush를 안하면, 에러없이 삭제됨.
		});
		assertThat(thrown, nullValue());
	}

	@DisplayName("팀에 맴버가 있는데 팀을 삭제하려는 에러가 발생하는 테스트")
	@Test
	void deleteRelateTeamErrorTest(){
		Team findTeam = em.find(Team.class, 1L);
		Throwable thrown = catchThrowable(()->{
			em.remove(findTeam); //문제는 삭제하면 에러 발생.
			em.flush(); //실제 DB에 적용하면 문제가 당연히 발생.
		});
		assertThat(thrown).isInstanceOf(PersistenceException.class); //엄밀히 말하면 PersistenceException ConstraintViolationException가 발생함.
	}

	@DisplayName("팀에 맴버를 삭제하고 팀을 삭제하려는 테스트")
	@Test
	void deleteRelateTeamTest(){
		Team findTeam = em.find(Team.class, 1L);
		Player findPlayer = em.find(Player.class, 2L);
		findPlayer.setTeam(null);
		Player findPlayer2 = em.find(Player.class, 1L);
		findPlayer2.setTeam(null);
		em.remove(findTeam);
		em.flush();

		Team deleteTeam = em.find(Team.class, 1L);
		assertThat(deleteTeam, nullValue());
	}
}
