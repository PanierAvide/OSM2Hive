/*
 * Copyright 2015 Adrien PAVIE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.pavie.osm2hive.model.xml;

/**
 * This exception is thrown when a {@link Markup} is not well-formed.
 * @author Adrien PAVIE
 */
public class InvalidMarkupException extends Exception {
//CONSTANTS
	private static final long serialVersionUID = 7634412370328792623L;

//CONSTRUCTORS
	public InvalidMarkupException() {
		super();
	}
	
	public InvalidMarkupException(String message) {
		super(message);
	}
}
