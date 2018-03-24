package com.luojilab.gen.router;

import com.hncgc1990.componentdemo.MainActivity;
import com.luojilab.component.componentlib.router.ui.BaseCompRouter;
import java.lang.Override;
import java.lang.String;

public class AppUiRouter extends BaseCompRouter {
  @Override
  public String getHost() {
    return "app";
  }

  @Override
  public void initMap() {
    super.initMap();
    routeMapper.put("/home",MainActivity.class);
  }
}

