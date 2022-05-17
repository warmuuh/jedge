package jedge.milkman.editor;

import static milkman.utils.FunctionalUtils.run;

import java.util.Arrays;
import javafx.scene.control.Tab;
import jedge.milkman.EdgeqlContentType;
import jedge.milkman.domain.EqlRequestAspect;
import milkman.domain.RequestContainer;
import milkman.ui.components.ContentEditor;
import milkman.ui.plugin.RequestAspectEditor;

public class JedgeRequestAspectEditor implements RequestAspectEditor {

	@Override
	public Tab getRoot(RequestContainer request) {
		EqlRequestAspect aspect = request.getAspect(EqlRequestAspect.class).get();
		Tab tab = new Tab("EdgeQL");
		
		ContentEditor editor = new ContentEditor();
		editor.setHeaderVisibility(false);
		editor.setEditable(true);
		editor.setContentTypePlugins(Arrays.asList(new EdgeqlContentType()));
		editor.setContentType("application/edgeql");
		editor.setContent(aspect::getEql, run(aspect::setEql).andThen(() -> aspect.setDirty(true)));
		
		tab.setContent(editor);
		return tab;
	}

	@Override
	public boolean canHandleAspect(RequestContainer request) {
		return request.getAspect(EqlRequestAspect.class).isPresent();
	}

}
