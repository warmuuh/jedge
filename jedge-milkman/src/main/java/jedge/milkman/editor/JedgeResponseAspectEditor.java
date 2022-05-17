package jedge.milkman.editor;

import java.util.List;
import javafx.scene.control.Tab;
import jedge.milkman.domain.JedgeResponseAspect;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import milkman.domain.RequestContainer;
import milkman.domain.ResponseContainer;
import milkman.ui.components.CodeFoldingContentEditor;
import milkman.ui.main.Toaster;
import milkman.ui.plugin.ContentTypeAwareEditor;
import milkman.ui.plugin.ContentTypePlugin;
import milkman.ui.plugin.ResponseAspectEditor;
import milkman.ui.plugin.ToasterAware;

@Slf4j
public class JedgeResponseAspectEditor implements ResponseAspectEditor, ContentTypeAwareEditor {

	private List<ContentTypePlugin> plugins;

	@Override
	@SneakyThrows
	public Tab getRoot(RequestContainer request, ResponseContainer response) {
		val body = response.getAspect(JedgeResponseAspect.class).orElseThrow(() -> new IllegalArgumentException("No edgedb response aspect"));
		CodeFoldingContentEditor root = new CodeFoldingContentEditor();
		root.setEditable(false);
		if (plugins != null)
			root.setContentTypePlugins(plugins);

		root.setContentType("application/json");
		root.setContent(body::getResult, value -> {});

		return new Tab("Response", root);
	}

	@Override
	public boolean canHandleAspect(RequestContainer request, ResponseContainer response) {
		return response.getAspect(JedgeResponseAspect.class).isPresent();
	}

	@Override
	public void setContentTypePlugins(List<ContentTypePlugin> plugins) {
		this.plugins = plugins;
	}

}
