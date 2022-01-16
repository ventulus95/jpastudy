package com.ventulus95.jpastudy;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ventulus95.jpastudy.domain.Player;
import com.ventulus95.jpastudy.domain.Team;

@DataJpaTest
public class OneToManyBiDirectTest {

	@PersistenceContext
	EntityManager em;

	@BeforeEach
	void beforeSetting(){
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

	@Test
	@DisplayName("일대다 조회하기")
	void findDirection(){
		Team team = em.find(Team.class, 1L);
		List<Player> list = team.getMembers();
		assertThat(list.size(), is(0)); //이러면 setTeam을 한경우 실제로 DB에는 팀이 잡혀있을것.
		Player player = em.find(Player.class, 1L);
		assertThat(player.getTeam().getName(), is("팀1")); //실제로 팀은 맴버에 잡혀있음
	}

	@Test
	@DisplayName("일대다 편의 메소드로 저장하기")
	void saveTeam(){
		Team team = em.find(Team.class, 1L);
		Player player = new Player(3L, "맴버3");
		player.addTeam(team);
		em.persist(player);
		assertThat(team.getMembers().size(), is(1)); //Add Team을 사용하면, 객체간의 관계성까지 같이 정의해서
		assertThat(team.getMembers().get(0).getName(), is("맴버3")); // 내부 맴버까지 삽입된다.
	}

	@Test
	@DisplayName("1대다 양뱡향 메소드를 끊지 않는 경우")
	void bugUpdateTeam(){
		Team team = em.find(Team.class, 1L);
		Player player = new Player(3L, "맴버3");
		player.addBugTeam(team);
		em.persist(player);
		Team team2 = new Team(2L, "팀2");
		em.persist(team2);
		player.addBugTeam(team2);
		em.persist(player);
		assertThat(team.getMembers().get(0).getName(), is("맴버3")); //실제로는 팀맴버가 없음
		assertThat(team2.getMembers().get(0).getName(), is("맴버3"));
		assertThat(team.getMembers().size(), is(1));
		assertThat(team2.getMembers().size(), is(1));
		Player player1 = em.find(Player.class, 3L);
		assertThat(player1.getTeam().getName(), is("팀2")); //변경은 되어있음 ㅋㅋ
	}
}
