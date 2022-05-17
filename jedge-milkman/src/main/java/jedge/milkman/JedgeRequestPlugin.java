package jedge.milkman;

import java.util.Collections;
import java.util.List;
import jedge.milkman.domain.EqlRequestAspect;
import jedge.milkman.domain.JedgeRequestContainer;
import jedge.milkman.editor.JedgeRequestAspectEditor;
import jedge.milkman.editor.JedgeRequestEditor;
import jedge.milkman.editor.JedgeResponseAspectEditor;
import milkman.domain.RequestContainer;
import milkman.domain.RequestExecutionContext;
import milkman.domain.ResponseContainer;
import milkman.ui.plugin.RequestAspectEditor;
import milkman.ui.plugin.RequestAspectsPlugin;
import milkman.ui.plugin.RequestTypeEditor;
import milkman.ui.plugin.RequestTypePlugin;
import milkman.ui.plugin.ResponseAspectEditor;
import milkman.ui.plugin.Templater;

public class JedgeRequestPlugin implements RequestTypePlugin, RequestAspectsPlugin {

	JedgeQueryProcessor processor = new JedgeQueryProcessor();

	@Override
	public RequestContainer createNewRequest() {
		return new JedgeRequestContainer("New EdgeQl Request", "");
	}

	@Override
	public RequestTypeEditor getRequestEditor() {
		return new JedgeRequestEditor();
	}

	@Override
	public ResponseContainer executeRequest(RequestContainer request, Templater templater) {
		return processor.executeRequest(request, templater);
	}

	@Override
	public List<RequestAspectEditor> getRequestTabs() {
		return List.of(new JedgeRequestAspectEditor());
	}

	@Override
	public List<ResponseAspectEditor> getResponseTabs() {
		return List.of(new JedgeResponseAspectEditor());
	}

	@Override
	public void initializeRequestAspects(RequestContainer request) {
		if (request instanceof JedgeRequestContainer) {
			if (request.getAspect(EqlRequestAspect.class).isEmpty()){
				request.addAspect(new EqlRequestAspect());
			}
		}
	}

	@Override
	public void initializeResponseAspects(RequestContainer request, ResponseContainer response, RequestExecutionContext context) {
		// we did it on our own, so nothing to do
	}

	@Override
	public String getRequestType() {
		return "EQL";
	}

	@Override
	public boolean canHandle(RequestContainer request) {
		return request instanceof JedgeRequestContainer;
	}

	@Override
	public int getOrder() {
		return 19;
	}

}
