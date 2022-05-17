package jedge.milkman;

import com.github.warmuuh.jedge.Dsn;
import com.github.warmuuh.jedge.Jedge;
import com.github.warmuuh.jedge.SimpleTypeRegistry;
import com.github.warmuuh.jedge.StringTypeDeserializer;
import com.github.warmuuh.jedge.WireFormat;
import java.io.IOException;
import java.util.Map;
import jedge.milkman.domain.EqlRequestAspect;
import jedge.milkman.domain.JedgeRequestContainer;
import jedge.milkman.domain.JedgeResponseAspect;
import jedge.milkman.domain.JedgeResponseContainer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import milkman.domain.RequestContainer;
import milkman.domain.ResponseContainer;
import milkman.domain.ResponseContainer.StyledText;
import milkman.ui.plugin.Templater;

@Slf4j
public class JedgeQueryProcessor {

	@SneakyThrows
	public ResponseContainer executeRequest(RequestContainer request, Templater templater) {

		if (!(request instanceof JedgeRequestContainer)) {
			throw new IllegalArgumentException("Unsupported request container: " + request.getType());
		}

		EqlRequestAspect eqlRequestAspect = request.getAspect(EqlRequestAspect.class)
				.orElseThrow(() -> new IllegalArgumentException("Missing Eql Aspect"));
		String finalEql = templater.replaceTags(eqlRequestAspect.getEql());


		JedgeRequestContainer jedgeRequest = (JedgeRequestContainer)request;
		String edgedbDsn = templater.replaceTags(jedgeRequest.getEdgeDbDsn());
		log.info("Executing Eql: " + finalEql);

		Dsn dsn = Dsn.fromString(edgedbDsn);
		return executeEql(finalEql, dsn);
	}

	private JedgeResponseContainer executeEql(String finalEql, Dsn conProps) throws IOException {
		JedgeResponseContainer response = new JedgeResponseContainer();
		JedgeResponseAspect responseAspect = new JedgeResponseAspect();

		long startTime = System.currentTimeMillis();
		long connectionTime = 0;
		long requestTime = 0;
		try(Jedge session = openSession(conProps)) {
			connectionTime = System.currentTimeMillis() - startTime;
			String result = String.join("\n", session.queryList(finalEql, new StringTypeDeserializer()));
			responseAspect.setResult(result);
			requestTime = System.currentTimeMillis() - startTime - connectionTime;
		} catch (IOException e) {
			log.error("failed to execute edgedb command", e);
			throw e;
		}

		response.getAspects().add(responseAspect);
		response.getStatusInformations().complete(Map.of(
				"Connection Time", new StyledText(connectionTime + "ms"),
				"Request Time", new StyledText(requestTime + "ms")));


		return response;
	}

	//TODO: need to cache and keep-alive sessions
	private Jedge openSession(Dsn conProps) throws IOException {
		Jedge jedge = new Jedge(WireFormat.JsonFormat, new SimpleTypeRegistry());
		jedge.connect(conProps);
		return jedge;
	}

}
