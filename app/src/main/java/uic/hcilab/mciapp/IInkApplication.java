// Copyright MyScript. All rights reserved.

package uic.hcilab.mciapp;

import android.app.Application;
import com.myscript.iink.Engine;
import uic.hcilab.mciapp.certificate.MyCertificate;

public class IInkApplication extends Application
{
  private static Engine engine;

  public static synchronized Engine getEngine()
  {
    if (engine == null)
    {
      engine = Engine.create(MyCertificate.getBytes());
    }

    return engine;
  }

}
