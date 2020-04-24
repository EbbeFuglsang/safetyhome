package com.ur.urcap.safetyhome.impl;

import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.domain.URCapAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.feature.Feature;
import com.ur.urcap.api.domain.feature.FeatureModel;
import com.ur.urcap.api.domain.program.ProgramModel;
import com.ur.urcap.api.domain.program.nodes.ProgramNodeFactory;
import com.ur.urcap.api.domain.program.nodes.builtin.MoveNode;
import com.ur.urcap.api.domain.program.nodes.builtin.WaypointNode;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.movenode.TCPSelection;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.movenode.builder.MoveLConfigBuilder;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.waypointnode.BlendParameters;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.waypointnode.VariablePositionDefinedWaypointNodeConfig;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.waypointnode.WaypointMotionParameters;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.waypointnode.WaypointNodeConfigFactory;
import com.ur.urcap.api.domain.program.structure.TreeNode;
import com.ur.urcap.api.domain.program.structure.TreeStructureException;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.util.Filter;
import com.ur.urcap.api.domain.validation.ErrorHandler;
import com.ur.urcap.api.domain.value.expression.Expression;
import com.ur.urcap.api.domain.value.expression.InvalidExpressionException;
import com.ur.urcap.api.domain.value.simple.Acceleration;
import com.ur.urcap.api.domain.value.simple.SimpleValueFactory;
import com.ur.urcap.api.domain.value.simple.Speed;
import com.ur.urcap.api.domain.variable.Variable;
import com.ur.urcap.api.domain.variable.VariableException;
import com.ur.urcap.api.domain.variable.VariableModel;
import com.ur.urcap.api.ui.annotation.Input;
import com.ur.urcap.api.ui.component.*;
import com.ur.urcap.api.ui.component.InputEvent.EventType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SafetyPositionProgramNodeContribution implements ProgramNodeContribution {

	private final URCapAPI api;
	private final DataModel model;

	private static final String NODE_TITLE_EN = "InitPos";

	private static final String INIT_POS = "initPos";

	private static final double SHARED_TOOL_SPEED = 250;
	private static final double SHARED_TOOL_ACCELERATION = 1200;

	/**
	 * テキストボックスの定義
	 */
	@Input(id = "inputNodeTitle")
	private InputTextField inputNodeTitle;

	@Input(id = "inputLowerX")
	private InputTextField inputLowerX;

	@Input(id = "inputUpperX")
	private InputTextField inputUpperX;

	@Input(id = "inputLowerY")
	private InputTextField inputLowerY;

	@Input(id = "inputUpperY")
	private InputTextField inputUpperY;

	@Input(id = "inputLowerZ")
	private InputTextField inputLowerZ;

	@Input(id = "inputUpperZ")
	private InputTextField inputUpperZ;

	@Input(id = "inputMoveX")
	private InputTextField inputMoveX;

	@Input(id = "inputMoveY")
	private InputTextField inputMoveY;

	@Input(id = "inputMoveZ")
	private InputTextField inputMoveZ;

	/**
	 * ラジオボタンの定義
	 */
	@Input(id = "inputBase")
	private InputRadioButton inputBase;

	@Input(id = "inputTool")
	private InputRadioButton inputTool;

	/**
	 * テキストボックスのイベントハンドラ
	 */
	@Input(id = "inputNodeTitle")
	private void onChange_inputNodeTitle(InputEvent event) {
		if (event.getEventType() == EventType.ON_CHANGE) {
			setModelonTextEvent("inputNodeTitle", inputNodeTitle);
		}
	}

	@Input(id = "inputLowerX")
	private void onChange_inputLowerX(InputEvent event) {
		if (event.getEventType() == EventType.ON_CHANGE) {
			setModelonTextEvent("inputLowerX", inputLowerX);
		}
	}

	@Input(id = "inputUpperX")
	private void onChange_inputUpperX(InputEvent event) {
		if (event.getEventType() == EventType.ON_CHANGE) {
			setModelonTextEvent("inputUpperX", inputUpperX);
		}
	}

	@Input(id = "inputLowerY")
	private void onChange_inputLowerY(InputEvent event) {
		if (event.getEventType() == EventType.ON_CHANGE) {
			setModelonTextEvent("inputLowerY", inputLowerY);
		}
	}

	@Input(id = "inputUpperY")
	private void onChange_inputUpperY(InputEvent event) {
		if (event.getEventType() == EventType.ON_CHANGE) {
			setModelonTextEvent("inputUpperY", inputUpperY);
		}
	}

	@Input(id = "inputLowerZ")
	private void onChange_inputLowerZ(InputEvent event) {
		if (event.getEventType() == EventType.ON_CHANGE) {
			setModelonTextEvent("inputLowerZ", inputLowerZ);
		}
	}

	@Input(id = "inputUpperZ")
	private void onChange_inputUpperZ(InputEvent event) {
		if (event.getEventType() == EventType.ON_CHANGE) {
			setModelonTextEvent("inputUpperZ", inputUpperZ);
		}
	}

	@Input(id = "inputMoveX")
	private void onChange_inputMoveX(InputEvent event) {
		if (event.getEventType() == EventType.ON_CHANGE) {
			setModelonTextEvent("inputMoveX", inputMoveX);
		}
	}

	@Input(id = "inputMoveY")
	private void onChange_inputMoveY(InputEvent event) {
		if (event.getEventType() == EventType.ON_CHANGE) {
			setModelonTextEvent("inputMoveY", inputMoveY);
		}
	}

	@Input(id = "inputMoveZ")
	private void onChange_inputMoveZ(InputEvent event) {
		if (event.getEventType() == EventType.ON_CHANGE) {
			setModelonTextEvent("inputMoveZ", inputMoveZ);
		}
	}

	/**
	 * ラジオボタンのイベントハンドラ
	 */
	@Input(id = "inputBase")
	private void onChange_inputBase(InputEvent event) {
		if (event.getEventType() == EventType.ON_CHANGE) {
			model.set("inputBase", inputBase.isSelected());
		}
	}

	@Input(id = "inputTool")
	private void onChange_inputTool(InputEvent event) {
		if (event.getEventType() == EventType.ON_CHANGE) {
			model.set("inputTool", inputTool.isSelected());
		}
	}

	/**
	 * フォームを最新に更新
	 */
	private void updateForm() {
		inputNodeTitle.setText(model.get("inputNodeTitle", ""));

		inputLowerX.setText(model.get("inputLowerX", "-1000"));
		inputUpperX.setText(model.get("inputUpperX", "1000"));
		inputLowerY.setText(model.get("inputLowerY", "-1000"));
		inputUpperY.setText(model.get("inputUpperY", "1000"));
		inputLowerZ.setText(model.get("inputLowerZ", "-1000"));
		inputUpperZ.setText(model.get("inputUpperZ", "1000"));

		inputMoveX.setText(model.get("inputMoveX", "0"));
		inputMoveY.setText(model.get("inputMoveY", "0"));
		inputMoveZ.setText(model.get("inputMoveZ", "0"));

		if (model.get("inputBase", true))
			inputBase.setSelected();
		else
			inputTool.setSelected();
	}

	/**
	 * 
	 * イベント処理時のデータモデルを更新する処理(Input Textイベント用)
	 * 
	 * @param name  コンポーネント名
	 * @param event イベントオブジェクト
	 */
	private void setModelonTextEvent(String name, InputTextField text) {

		if (!text.getText().isEmpty())
			model.set(name, text.getText());
		else
			model.remove(name);

	}

	/* コンストラクタ */
	public SafetyPositionProgramNodeContribution(URCapAPI api, DataModel model) {
		this.api = api;
		this.model = model;

		try {
			ProgramModel pm = api.getProgramAPIProvider().getProgramAPI().getProgramModel();
			FeatureModel fm = api.getProgramAPIProvider().getProgramAPI().getFeatureModel();
			TreeNode root = pm.getRootTreeNode(this);
			ProgramNodeFactory nf = pm.getProgramNodeFactory();
			SimpleValueFactory svf = api.getProgramAPIProvider().getProgramAPI().getValueFactoryProvider()
					.getSimpleValueFactory();

			// InitPosition変数の定義：無い場合のみ
			VariableModel vm = api.getProgramAPIProvider().getProgramAPI().getVariableModel();

			Filter<Variable> filter = new Filter<Variable>() {
				@Override
				public boolean accept(Variable variable) {
					return variable.getType().equals(Variable.Type.GLOBAL)
							|| variable.getType().equals(Variable.Type.VALUE_PERSISTED);
				};
			};

			Collection<Variable> variables = vm.get(filter);
			Variable varPos = null;
			boolean isExistInitPos = false;

			for (Iterator<Variable> iterator = variables.iterator(); iterator.hasNext();) {
				Variable var = iterator.next();

				if (var.getDisplayName().equals(INIT_POS)) {
					varPos = var;
					isExistInitPos = true;
					break;
				}
			}

			if (!isExistInitPos) {

				// 変数の生成
				Expression expression = api.getProgramAPIProvider().getProgramAPI().getValueFactoryProvider()
						.createExpressionBuilder().append("p[0,0,0,0,0,0]").build();
				varPos = vm.getVariableFactory().createGlobalVariable(INIT_POS, expression);
			}

			// MoveNode生成
			MoveNode moveNode = nf.createMoveNodeNoTemplate();

			Speed speed = svf.createSpeed(SHARED_TOOL_SPEED, Speed.Unit.MM_S);
			Acceleration acceleration = svf.createAcceleration(SHARED_TOOL_ACCELERATION, Acceleration.Unit.MM_S2);
			Feature feature = fm.getBaseFeature();
			TCPSelection tcpSelection = moveNode.getTCPSelectionFactory().createActiveTCPSelection();

			MoveLConfigBuilder moveLConfigBuilder = moveNode.getConfigBuilders().createMoveLConfigBuilder()
					.setToolSpeed(speed, ErrorHandler.AUTO_CORRECT)
					.setToolAcceleration(acceleration, ErrorHandler.AUTO_CORRECT).setFeature(feature)
					.setTCPSelection(tcpSelection);

			moveNode.setConfig(moveLConfigBuilder.build());

			// Waypoint生成
			WaypointNode waypointNode = nf.createWaypointNode();
			WaypointNodeConfigFactory waypointNodeConfigFactory = waypointNode.getConfigFactory();
			BlendParameters blendParameters = waypointNodeConfigFactory.createNoBlendParameters();
			WaypointMotionParameters motionParameters = waypointNodeConfigFactory.createSharedMotionParameters();

			VariablePositionDefinedWaypointNodeConfig waypointNodeConfig = waypointNodeConfigFactory
					.createVariablePositionConfig(varPos, blendParameters, motionParameters);

			waypointNode.setConfig(waypointNodeConfig);

			TreeNode moveTreeNode = root.addChild(moveNode);
			moveTreeNode.addChild(waypointNode);

		} catch (VariableException e) {

		} catch (InvalidExpressionException e) {

		} catch (TreeStructureException e) {

		}

	}

	/* 設定画面を表示したときの処理 */
	@Override
	public void openView() {

		updateForm();
		this.getTitle();

	}

	/**
	 * スクリプトファイルの読み込み
	 * 
	 * @param filename ファイル名
	 * @return 文字列の配列
	 */
	public String[] readScriptFile(String filename) {
		try {

			BufferedReader br = new BufferedReader(
					new InputStreamReader(this.getClass().getResourceAsStream(filename)));

			ArrayList<String> list = new ArrayList<String>();

			String addstr;
			while ((addstr = br.readLine()) != null) {
				list.add(addstr);
			}

			br.close();
			String[] res = list.toArray(new String[0]);
			return res;

		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public void closeView() {

	}

	@Override
	public String getTitle() {

		return NODE_TITLE_EN + ":" + model.get("inputNodeTitle", "");

	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public void generateScript(ScriptWriter writer) {

		String[] scripts = readScriptFile("/com/ur/urcap/safetyhome/impl/safetyhome.script");

		for (String script : scripts) {
			script = script.replace("{inputLowerX}", model.get("inputLowerX", "-1000"));
			script = script.replace("{inputUpperX}", model.get("inputUpperX", "1000"));
			script = script.replace("{inputLowerY}", model.get("inputLowerY", "-1000"));
			script = script.replace("{inputUpperY}", model.get("inputUpperY", "1000"));
			script = script.replace("{inputLowerZ}", model.get("inputLowerZ", "-1000"));
			script = script.replace("{inputUpperZ}", model.get("inputUpperZ", "1000"));

			script = script.replace("{inputMoveX}", model.get("inputMoveX", "0"));
			script = script.replace("{inputMoveY}", model.get("inputMoveY", "0"));
			script = script.replace("{inputMoveZ}", model.get("inputMoveZ", "0"));

			script = script.replace("{baseAxis}", model.get("inputBase", true) ? "True" : "False");

			writer.appendLine(script);
		}

		writer.writeChildren();

	}

}