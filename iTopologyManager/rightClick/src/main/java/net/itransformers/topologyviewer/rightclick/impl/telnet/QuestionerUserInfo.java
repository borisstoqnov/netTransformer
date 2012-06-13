/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * 
 */
package net.itransformers.topologyviewer.rightclick.impl.telnet;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import com.wittams.gritty.Questioner;

class QuestionerUserInfo implements UserInfo, UIKeyboardInteractive {
	private Questioner questioner;
	private String password;
	private String passPhrase;
	
	public QuestionerUserInfo(Questioner questioner){
		this.questioner = questioner;
	}
	
	public String getPassphrase(){
		return passPhrase;
	}

	public String getPassword(){
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public boolean promptPassphrase(String message){
		passPhrase = questioner.questionHidden(message + ":");
		return true;
	}

	public boolean promptPassword(String message){
		password = questioner.questionHidden(message + ":");
		return true;
	}

	public boolean promptYesNo(String message){
		String yn = questioner.questionVisible(message + " [Y/N]:" , "Y");
		String lyn = yn.toLowerCase();
		if( lyn.equals("y") || lyn.equals("yes") ){
			return true;
		}else{
			return false;
		}
	}

	public void showMessage(String message){
		questioner.showMessage(message);
	}

	public String[] promptKeyboardInteractive(final String destination, final String name,
			final String instruction, final String[] prompt, final boolean[] echo){
		int len = prompt.length;
		String [] results = new String[len];
		if(destination != null && destination.length() > 0 ) questioner.showMessage(destination);
		if(name != null && name.length() > 0 ) questioner.showMessage(name);
		questioner.showMessage(instruction);
		for(int i = 0; i < len ; i++ ){
			String promptStr = prompt[i];
			results[i] = echo[i] ? questioner.questionVisible(promptStr, null) :
								   questioner.questionHidden(promptStr) ;
		}
		return results;
	}

}
