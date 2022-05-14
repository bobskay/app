package a.b.c.base.utils;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 主键生成器 0正负标识, 1-40当前毫秒值,41-52同一毫秒可产生的序列号, 53-63机器编号, 010001101011110010001111000100111011
 * 000000001110 0000000000
 *
 * @author huawei
 */
public class IdWorker {

  private static IdWorker INSTANCE = new IdWorker();
  private Logger log = LoggerFactory.getLogger(this.getClass());

  public IdWorker() {
    this(0);
  }

  /**
   * 机器位数
   */
  public static final int MACHINE_BITS = 10;
  /**
   * 序列号位数
   */
  public static final int SEQ_BITS = 12;
  /**
   * 时间往左移动位数
   */
  public static final int TIME_SHIFT = MACHINE_BITS + SEQ_BITS;
  /**
   * 最大允许序列号(4095)
   */
  public static final long MAX_SEQ = -1L ^ (-1L << SEQ_BITS);
  /**
   * 最大允许机器号(1023),最小0
   */
  public static final long MAX_MACHINE = -1L ^ (-1L << MACHINE_BITS);
  /**
   * 为了防止超过long的最大值,时间戳生成后减去这个值
   */
  private static final long BEGIN = new DateTime("2019-01-01").getTime();
  private int machineCode;
  private long lastTime;
  /**
   * 因为0无法向左做位移操作,序列号从1开始
   */
  private long sequence = 1;

  /**
   * @param machineCode 机器编号
   */
  public IdWorker(int machineCode) {
    this.machineCode = machineCode;
    if (machineCode > MAX_MACHINE) {
      throw new RuntimeException("机器编号不能大于" + MAX_MACHINE);
    }
    if ((currentTimeMillis() - BEGIN) << TIME_SHIFT < 0) {
      // 2086-09-06
      long max = (Long.MAX_VALUE >> TIME_SHIFT) + BEGIN;
      throw new RuntimeException("当前时间超过" + new DateTime(max) + ",生成器已无法使用");
    }
    if (machineCode != 0) {
      log.info("主键生成器创建完毕,当前机器号:" + machineCode);
    }
  }

  @SneakyThrows
  public synchronized long nextId() {
    long time = currentTimeMillis() - BEGIN;
    if (time != lastTime) {
      lastTime = time;
      sequence = 1;
    } else {
      sequence++;
    }
    if (sequence > MAX_SEQ) {
      Thread.sleep(1);
      return nextId();
    }
    //时间向左移动22位+机器向左移动12位+机器的10位
    return (time << TIME_SHIFT) | (sequence << MACHINE_BITS) | machineCode;
  }

  /**
   * 获取当前时间单独弄一个方法,方便测试时覆盖
   *
   * @return
   */
  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }

  /**
   * 通过生成的id获取id生成时间,去掉后面的22位
   *
   * @param id
   * @return
   */
  public static long getTimestamp(long id) {
    return BEGIN + (id >>> TIME_SHIFT);
  }

  public static long createByTime(long time) {
    time = time - BEGIN;
    return (time << TIME_SHIFT) | (0 << MACHINE_BITS) | 0;
  }

  /**
   * 通过生成的id获得机器编号,取最后10位
   */
  public static int getMachineNo(long id) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < MACHINE_BITS; i++) {
      sb.append(id & 1);
      id = id >>> 1;
    }
    return Integer.parseInt(sb.reverse().toString(), 2);
  }

  public static int getMachineNo(String id) {
    return getMachineNo(toLong(id));
  }

  public static long nextLong() {
    return INSTANCE.nextId();
  }

  public static String nextString() {
    long l = INSTANCE.nextId();
    return Long.toString(l, Character.MAX_RADIX);
  }

  public static Long toLong(String uuid) {
    return Long.parseLong(uuid, Character.MAX_RADIX);
  }
}