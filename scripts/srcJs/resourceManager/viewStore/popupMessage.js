import Reflux from "reflux";

import db from "../database";

import Actions from "../actions";

class PopupMessage extends Reflux.Store {
	constructor()
	{
		super();
		this.state = {
            userGroups: db.get("userGroups").value()
        };
		this.listenToMany(Actions.view.global.popupMessage);
	}


}

export default new PopupMessage();