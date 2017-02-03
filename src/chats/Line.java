package chats;

import java.util.*;

import entities.*;
import menus.*;

/**
 * How a conversation runs.
 * How an NPC follows their 'lines' 
 * @author Jon
 */
public class Line {
	private String prompt, text, conditionParameter;
	private List<Integer> answers;
	private int id;
	private Conditions condition;
	private Actions action;

	public Line(int id, String prompt, String text, Conditions condition, String conditionParameter, List<Integer> answers,
	        Actions action)
	{
		this.id = id;
		this.prompt = prompt;
		this.text = text;
		this.conditionParameter = conditionParameter;
		this.answers = answers;
		this.condition = condition;
		this.action = action;
	}

	public int getID() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getPrompt() {
		return prompt;
	}

	public String getConditionParameter() {
		return conditionParameter;
	}

	public List<Integer> getRepsonses() {
		return answers;
	}

	public Conditions getCondition() {
		return condition;
	}

	public Actions getAction() {
		return action;
	}

	public Line display( NPC npc, Player player, List<Line> lines ) {
		if (answers.size() == 0) return null;
		List<MenuType> answerList = new ArrayList<>();
		for (Integer answerNum : answers) {
			Line answer = lines.get(answerNum);
			if (ChatManager.matchesConditions(npc, player, answer)) {
				answerList.add(new MenuType(answer.getPrompt(), null));
			}
		}
		Menus answerMenu = new Menus();
		MenuType answer = answerMenu.displayMenu(answerList);
		for (int answerNum : answers) {
			Line possibleAnswer = lines.get(answerNum);
			if (possibleAnswer.getPrompt().equals(answer.getCommand())) ;
		}
		return null;
	}
}