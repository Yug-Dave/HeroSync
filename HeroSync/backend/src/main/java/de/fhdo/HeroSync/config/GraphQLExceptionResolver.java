package de.fhdo.HeroSync.config;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@SuppressWarnings("null")
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {

  @Override
  protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {

    if (ex instanceof ResponseStatusException rse) {
      int code = rse.getStatusCode().value();

      String classification = switch (code) {
        case 400 -> "BAD_REQUEST";
        case 401, 403 -> "UNAUTHORIZED";
        case 404 -> "NOT_FOUND";
        default -> "INTERNAL_ERROR";
      };

      String message = (rse.getReason() != null) ? rse.getReason() : rse.getStatusCode().toString();

      return GraphqlErrorBuilder.newError(env)
        .message(message)
        .extensions(java.util.Map.of("classification", classification))
        .build();
    }

    return null; // fallback to default behavior for other exceptions
  }
}
