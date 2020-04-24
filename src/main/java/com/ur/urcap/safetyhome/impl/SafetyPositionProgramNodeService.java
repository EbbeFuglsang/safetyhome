package com.ur.urcap.safetyhome.impl;

import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.ProgramNodeService;
import com.ur.urcap.api.domain.URCapAPI;
import com.ur.urcap.api.domain.data.DataModel;

import java.io.InputStream;

public class SafetyPositionProgramNodeService implements ProgramNodeService {
	@Override
	public String getId() {
		return "SafetyPosition";
	}

	@Override
	public boolean isDeprecated() {
		return false;
	}

	@Override
	public boolean isChildrenAllowed() {
		return true;
	}

	@Override
	public String getTitle() {
		return "Go Safety Position";
	}

	@Override
	public InputStream getHTML() {

		return this.getClass().getResourceAsStream("/com/ur/urcap/safetyhome/impl/SafetyHomeProgramNode_en.html");

	}

	@Override
	public ProgramNodeContribution createNode(URCapAPI api, DataModel model) {
		return new SafetyPositionProgramNodeContribution(api, model);
	}
}
