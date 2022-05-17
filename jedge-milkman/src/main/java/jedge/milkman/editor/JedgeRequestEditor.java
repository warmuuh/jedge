package jedge.milkman.editor;

import com.jfoenix.controls.JFXTextField;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.SneakyThrows;
import milkman.domain.RequestContainer;
import jedge.milkman.domain.JedgeRequestContainer;
import milkman.ui.components.AutoCompleter;
import milkman.ui.plugin.AutoCompletionAware;
import milkman.ui.plugin.RequestTypeEditor;
import milkman.utils.fxml.FxmlBuilder.HboxExt;
import milkman.utils.fxml.GenericBinding;

public class JedgeRequestEditor implements RequestTypeEditor, AutoCompletionAware {


	TextField edgedbDsn;
	
	private GenericBinding<JedgeRequestContainer, String> dsnBinding = GenericBinding.of(JedgeRequestContainer::getEdgeDbDsn, JedgeRequestContainer::setEdgeDbDsn);

	private AutoCompleter completer;

	@Override
	@SneakyThrows
	public Node getRoot() {
		Node root = new JedgeRequestEditorFxml(this);
		return root;
	}

	@Override
	public void displayRequest(RequestContainer request) {
		if (!(request instanceof JedgeRequestContainer))
			throw new IllegalArgumentException("Other request types not yet supported");

		JedgeRequestContainer restRequest = (JedgeRequestContainer)request;
		
		dsnBinding.bindTo(edgedbDsn.textProperty(), restRequest);
		dsnBinding.addListener(s -> request.setDirty(true));
		completer.attachVariableCompletionTo(edgedbDsn);
		
	}

	@Override
	public void setAutoCompleter(AutoCompleter completer) {
		this.completer = completer;
	}

	public static class JedgeRequestEditorFxml extends HboxExt {
		private JedgeRequestEditor controller; //avoid gc collection

		public JedgeRequestEditorFxml(JedgeRequestEditor controller) {
			this.controller = controller;
			HBox.setHgrow(this, Priority.ALWAYS);
			controller.edgedbDsn = add(new JFXTextField(), true);
			controller.edgedbDsn.setId("edgedbUrl");
			controller.edgedbDsn.setPromptText("edgedb://user:pass@host:port/database");
		}
	}

}
