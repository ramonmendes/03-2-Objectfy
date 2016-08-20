/**
 * Copyright 2014-2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//[START all]
package com.example.guestbook;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.KeyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.LoadType;

/**
 * Form Handling Servlet Most of the action for this sample is in
 * webapp/guestbook.jsp, which displays the {@link Greeting}'s. This servlet has
 * one method {@link #doPost(<#HttpServletRequest req#>, <#HttpServletResponse
 * resp#>)} which takes the form data and saves it.
 */
public class ConsultasGuestbookServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3533303093779285412L;

	// Process the http POST of the form
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String operation = req.getParameter("operation");
		String content = req.getParameter("content");
		String qtde = req.getParameter("qtde");
		String parent = req.getParameter("parent");

		List<Greeting> list  = null;
		
		if(null != operation && operation.length() > 0){
			LoadType<Greeting> query = ObjectifyService.ofy().load()
					.type(Greeting.class);
			Long qtdeLong = null;
			if(qtde != null){
				qtdeLong = Long.valueOf(qtde);
			}
			
			if(operation.equals("igual")){
				list = query.filter("content = ", content).list(); //Equality filter
			}
			if(operation.equals("igual2")){
				list = query.filter("qtde", qtdeLong).list(); //Equality filter
			}
			if(operation.equals("diferente")){
				list = query.filter("content != ", content).list(); //Inequality filter
			}
			if(operation.equals("ancestor")){
				list = query.ancestor(KeyFactory.createKey(Guestbook.class.getSimpleName(), parent)).list(); //parent
			}
			
			if(operation.equals("composto")){
				list = query
						.filter("content", content)
						.filter("qtde !=", qtdeLong)
						.order("qtde")
						.list(); //Composto
			}
		}
		
		print(resp,operation, list);
	}

	private void print(HttpServletResponse resp, String operation, List<Greeting> list)
			throws IOException {
		//HTML
		String init = "<html><head><title>Resultado</title></head><body><table>"
				+ "<thead>"
				+ "<tr>"
				+ "<td>Content ::: Operation : "+operation+"</td>"
				+ "</tr></thead>"
				+ "<tbody>" ;
		String fim = "</tr></tbody></table></body></html>";

		String meio = "";

		if (list != null && list.size() > 0) {
			for (Greeting greeting : list) {
				meio += "<tr><td>" + greeting.content + "</td><td>"+greeting.qtde+"</td></tr>";
			}
			resp.getWriter().append(init + meio + fim);

		} else {
			resp.getWriter().append("Sem dados!");
		}
	}
}
// [END all]
