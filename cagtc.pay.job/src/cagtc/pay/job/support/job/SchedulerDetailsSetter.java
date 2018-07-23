package cagtc.pay.job.support.job;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SchedulerDetailsSetter
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerDetailsSetter.class);

  static void setDetails(Object target, String schedulerName, String schedulerId)
    throws SchedulerException
  {
    set(target, "setInstanceName", schedulerName);
    set(target, "setInstanceId", schedulerId);
  }

  private static void set(Object target, String method, String value) throws SchedulerException
  {
    Method setter;
    try
    {
      setter = target.getClass().getMethod(method, new Class[] { String.class });
    } catch (SecurityException e) {
      LOGGER.error("A SecurityException occured: " + e.getMessage(), e);
      return;
    }
    catch (NoSuchMethodException e) {
      LOGGER.warn(target.getClass().getName() + " does not contain public method " + method + "(String)");

      return;
    }

    if (Modifier.isAbstract(setter.getModifiers()))
    {
      LOGGER.warn(target.getClass().getName() + " does not implement " + method + "(String)");

      return;
    }
    try
    {
      setter.invoke(target, new Object[] { value });
    } catch (InvocationTargetException ite) {
      throw new SchedulerException(ite.getTargetException());
    } catch (Exception e) {
      throw new SchedulerException(e);
    }
  }
}