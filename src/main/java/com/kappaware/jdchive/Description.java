/*
 * Copyright (C) 2016 BROADSoftware
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kappaware.jdchive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.esotericsoftware.yamlbeans.YamlConfig;

public class Description {
	public enum State {
		present, absent
	}

	public enum OwnerType {
		user, group, role, USER, GROUP, ROLE
	}

	static YamlConfig yamlConfig = new YamlConfig();
	static {
		yamlConfig.setPropertyElementType(Description.class, "databases", Database.class);
		yamlConfig.setPropertyElementType(Description.class, "tables", Table.class);
	}

	static public YamlConfig getYamlConfig() {
		return yamlConfig;
	}

	public List<Database> databases;
	public List<Table> tables;

	void polish(State defaultState) throws DescriptionException {
		if (databases != null) {
			for (Database db : this.databases) {
				db.polish(defaultState);
			}
		}
		if (tables != null) {
			for (Table tbl : this.tables) {
				tbl.polish(defaultState);
			}
		}
	}

	static public class Database {
		public String name;
		public Map<String, String> properties;
		public String location;
		public String owner_name;
		public OwnerType owner_type;
		public String comment;
		public State state;

		void polish(State defaultState) throws DescriptionException {
			if (this.name == null) {
				throw new DescriptionException("Invalid description: Every database must have a 'name' attribute");
			}
			if ("default".equals(this.name)) {
				throw new DescriptionException("Can't alter 'default' database");
			}
			if (this.location == null) {
				throw new DescriptionException(String.format("Invalid description: Database '%s' is missing 'location' attribute", this.name));
			}
			if (!this.location.startsWith("/")) {
				throw new DescriptionException(String.format("Invalid description: Database '%s' location must be absolute", this.name));
			}
			if (this.properties == null) {
				this.properties = new HashMap<String, String>();
			}
			if (this.state == null) {
				this.state = defaultState;
			}
		}

	}

	static public class Table {
		public String name;
		public String database;
		public boolean external;
		public String owner;
		public String comment;
		public Map<String, String> properties;
		public List<Field> fields;
		public String location;
		public String fileFormat;
		public String storageHandler;
		public String inputFormat;
		public String outputFormat;
		public String serde;
		public Map<String, String> serdeProperties;
		public State state;

		void polish(State defaultState) throws DescriptionException {
			if (this.name == null) {
				throw new DescriptionException("Invalid description: Every table must have a 'name' attribute");
			}
			if (this.database == null) {
				throw new DescriptionException(String.format("Invalid description: Table '%s' is missing database attribute!", this.name));
			}
			if (this.fields == null) {
				this.fields = new Vector<Field>();
			}
			for (Field f : this.fields) {
				f.polish(this.name, defaultState);
			}
			if (this.properties == null) {
				this.properties = new HashMap<String, String>();
			}
			if (this.comment != null) {
				this.properties.put("comment", this.comment);
			}
			if ((this.inputFormat == null) !=  (this.outputFormat == null)) {
				throw new DescriptionException(String.format("Invalid description: Table '%s.%s': Both 'inputFormat' and 'outputFormat' must be defined together!", this.database, this.name));
			}
			if (this.serdeProperties == null) {
				this.serdeProperties = new HashMap<String, String>();
			}
			if (this.state == null) {
				this.state = defaultState;
			}
		}
	}

	static public class Field {
		public String name;
		public String type;
		public String comment;

		void polish(String tableName, State defaultState) throws DescriptionException {
			if (this.name == null) {
				throw new DescriptionException(String.format("Invalid description: Table '%s'. Missing 'name' attribute in some field(s)!", tableName));
			}
			if (this.type == null) {
				throw new DescriptionException(String.format("Invalid description: Table '%s'. Missing 'type' attribute for field '%s'!", tableName, this.name));
			}
		}
	}

}