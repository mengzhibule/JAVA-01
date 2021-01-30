package com.shawn.study.nio.gateway.router;

import java.util.List;

public interface Router {
  String route(List<String> endpoints);
}
