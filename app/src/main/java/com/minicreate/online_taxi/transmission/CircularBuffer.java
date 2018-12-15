package com.minicreate.online_taxi.transmission;

import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;

import java.util.LinkedList;
import java.util.List;

public class CircularBuffer {
    private final String TAG = "CircularBuffer";
    private byte[] buffer;
    private int readPos;
    private int writePos;
    private int capacity;    // 容量
    private int readableSize;        // 当前可讀元素个数
//	private boolean debug = true;

    private List<Integer> packageLenList = new LinkedList<Integer>();
    private List<byte[]> packageQueue = new LinkedList<byte[]>();

    public CircularBuffer(int cap) {
        buffer = new byte[cap];
        readPos = writePos = 0;
        readableSize = 0;
        capacity = cap;
        System.out.println("CircularBuffer capcity = " + capacity);
    }

    public void reset() {
        readPos = 0;
        writePos = 0;
        readableSize = 0;
    }

    /**
     * @param
     * @param len
     * @return 写入成功返回0，其他-1
     */
    public synchronized int writeArray(byte[] des, int len) {
        //TODO 空间不够了就返回-1，不需要做额外处理么？比如扩容或者清空缓存数据，如果一直
        if (!isSpaceEnough(len)) {
            return -1;
        }
        d("写之前的size: " + readableSize + " writePos: " + writePos);
        for (int i = 0; i < len; i++) {
            writeByte(des[i]);
        }
        d("写后的size: " + readableSize + " writePos: " + writePos);
        return 0;
    }

    /**
     * 写入单个byte，如果空间满了，那么就将下标置为0，重头开始写入
     *
     * @param a
     */
    private void writeByte(byte a) {
        if (writePos == capacity) {
            writePos = 0;
            d("writePos set 0");
        }
        buffer[writePos++] = a;
        // buf[writePos++ % capacity] = a;
        ++readableSize;
    }

    public synchronized byte[] readArray(int len) {
        if (IsEmpty() || len > capacity || len > readableSize) {
            d("ReadArray error!");
            return null;
        }
        d("读之前的readableSize: " + readableSize + " readPos: " + readPos);
        byte[] temp = new byte[len];
        for (int i = 0; i < len; i++) {
            temp[i] = readByte();
        }
        d("读后的readableSize: " + readableSize + " readPos: " + readPos);
        return temp;
    }

//	public synchronized byte[] readPackage(int len) {
//		if (IsEmpty() || len>capacity || len>size) {
//			d("ReadArray error!");
//			return null;
//		}
//		// 找到包头，收到有效包前，可能收到其他垃圾数据
//		// 如果垃圾数据包含 0x7e,就没办法了
//		byte value = 0;
//		int index = 0;
//		for ( ; index < len; index++) {
//			if((value = readByte()) == ProtocolVh.SYNC_FLAG_HEADER) {
//				d("找到包头0x7e：" + index);
//				break;
//			}
//		}
//		int vaildLen = len-index;
//		d("读包的长度：" + len + " vaildLen: " + vaildLen);
//		byte[] temp = new byte[vaildLen];
//		temp[0] = value;
//		for (int i = 1; i < vaildLen; i++) {
//			temp[i] = readByte();
//		}
//		return temp;
//	}

    private byte readByte() {
        if (readPos == capacity) {
//			LogUtil.w("buff","读一圈了，从头开始");
            readPos = 0;
            d("readPos set 0");
        }
        --readableSize;
        return buffer[readPos++];
    }

//	public byte[] ReadArrayFrom(int start, int len) {
//		byte[] temp = new byte[len];
//		for (int i = 0; i < len; i++) {
//			temp[i] = ReadAt(start + i);
//		}
//		size -= len;
//		// info();
//		return temp;
//	}
//
//	public byte ReadAt(int start) {
//		if (start < capacity) {
//			byte val = buffer[start];
//			// info();
//			return val;
//		}
//		return 0x00;
//	}

    /**
     * 在缓冲区找到某个数，返回其位置
     *
     * @param des
     * @return
     */
    public int find(byte des) {
        int cnt = readableSize;
        int pos = readPos;
        while (cnt-- > 0 && (readPos != writePos)) {
            if (pos == capacity) {
                pos = 0;
            }
            if (buffer[pos++] == des) {
                return (--pos);
            }
        }
        return -1;// not found
    }

    /**
     * 在缓冲区找到某对数，返回其位置
     *
     * @param
     * @return
     */
    public int findPair(byte a, byte b) {
        int cnt = readableSize;
        int pos = readPos;
        while (cnt-- > 0) {
            if (pos == capacity) {
                pos = 0;
            }
            if (buffer[pos] == a && buffer[(pos + 1)] == b) {
                return pos;
            }
            pos++;
        }
        return -1;// not found
    }

    public int findCnt(byte des) {
        int cnt = readableSize;
        int pos = readPos;
        for (int i = 0; i < cnt; i++) {

        }
        while (cnt-- > 0 && (readPos != writePos)) {
            if (pos == capacity) {
                pos = 0;
            }
            if (buffer[pos++] == des) {
                return (--pos);
            }
        }
        return -1;// not found
    }

    public boolean IsEmpty() {
        if (readableSize == 0) {
            d("IsEmpty!");
            return true;
        }
        return false;
    }

    public boolean IsFull() {
        if (readableSize == capacity) {
            d("IsFull!");
            return true;
        }
        return false;
    }

    private int getWriteAndReadDis() {
        if (writePos < readPos) {
            return ((capacity + writePos) - readPos);
        } else {
            return (writePos - readPos);
        }
    }

    /**
     * 是否有足够的空间写入新数据
     *
     * @param writeLen
     * @return
     */
    public boolean isSpaceEnough(int writeLen) {
        if ((readableSize + writeLen) > capacity) {
            d("IsWriteEnough!");
            return false;
        }
        return true;
    }

    /**
     * 缓冲区的数据，有否有完整的一个包
     * 检查到有一包完整数据，必须马上读走，否则可能出问题
     *
     * @param
     * @return
     */
    public byte[] checkFullPackage(byte checkCode) {
        int len = findPackageFlag(checkCode);
        if (len > 0) {
            d("完整包：" + len);
            return readArray(len);
        }
        d("不是完整包：" + len);
        return null;
    }

    public List<byte[]> checkFullPackage2(byte checkCode) {
        List<Integer> len = findPackageFlag2(checkCode);
        if (len == null || len.size() == 0) {
            d("不是完整包：" + len);
            return null;
        }
        packageQueue.clear();
        for (Integer l : len) {
            LogUtil.w("buff", "完整包：" + l);
            packageQueue.add(readArray(l));
        }
        return packageQueue;
    }

    /**
     * // 找到了头和尾的包标志位
     *
     * @param des
     * @return 完整包的长度
     */
    private int findPackageFlag(byte des) {
        int number = 0;
        int cnt = readableSize;
        int pos = readPos;
        int secondFlagPos = 0; // 第二个标志位的位置

        for (int i = 0; i < cnt; i++) {
            if (pos == capacity) {
                pos = 0;
            }
            if (buffer[pos++] == des) {
                number++;
                if (number == 2) {
                    secondFlagPos = i + 1;
                }
            }
        }
        if (number == 0) {
            // 收到数据后 从缓冲区读起始位到这个包末尾，都没有包标志，属于垃圾数据，（清除）
            readableSize = readableSize - getWriteAndReadDis();
            writePos = readPos;
            d("收到数据后 从缓冲区读起始位到这个包末尾，都没有包标志，属于垃圾数据（清除）");
            d("清空后的size: " + readableSize + " writePos: " + writePos);
            return -1;
        } else if (number == 1) {
            // 收到包的前半截数据
            // 也有可能是带标志位的垃圾数据
            d("number == 1 收到包的前半截数据 ");
            return 0;
        } else if (number == 2) {
            // 收到整包数据
            // 也有可能是带标志位的垃圾数据
            return secondFlagPos;
        } else if (number == 3) { // 之前收到半个包，已经有了一个包标志，
            // 现在来了一个包结束，以及一个新包的前半包，共3个包标志
            return secondFlagPos;
        } else if (number == 4) { // 之前收到半个包，已经有了一个包标志，
            // 现在来了一个包结束，以及一个新包的前半包，共3个包标志
            return secondFlagPos;
        } else {
            // 大于两个包标志位，说明有垃圾数据（清除）
            // socket 每次发一个包过来，最多只有两个包标志
            readableSize = readableSize - getWriteAndReadDis();
            writePos = readPos;
            d("大于两个包标志位，说明有垃圾数据（清除）");
            d("-3清空后的size: " + readableSize + " writePos: " + writePos);
            return -3;
        }
    }

    private List<Integer> findPackageFlag2(byte des) {
        int number = 0;
        int cnt = readableSize;
        int pos = readPos;
        int secondFlagPos = 0; // 第二个标志位的位置
        int flagPosbackup = 0;

        packageLenList.clear(); // 待考虑并发问题，目前是阻塞读socket，可以不考虑

        for (int i = 0; i < cnt; i++) {
            if (pos == capacity) {
//				LogUtil.w("buff","已经读了一圈");
                pos = 0;
            }
            if (buffer[pos++] == des) {// pos大于buff容量的判断，前两行代码已经判断了
                number++;
                if (number % 2 == 0) { // 前面已经++ 所以最小为1
                    secondFlagPos = (i + 1 - flagPosbackup); // 减去上一个包标志位的index，得到当前包的长度
                    flagPosbackup = i + 1;
                    packageLenList.add(secondFlagPos);
                }
            }
        }
		/*if(number == 0) {// 并不一定是垃圾数据
			// 收到数据后 从缓冲区读起始位到这个包末尾，都没有包标志，属于垃圾数据，（清除）
			readableSize = readableSize- getWriteAndReadDis();
			writePos = readPos;
			d("收到数据后 从缓冲区读起始位到这个包末尾，都没有包标志，属于垃圾数据（清除）");
			d("清空后的size: " + readableSize + " writePos: " + writePos);
			return null;
		}
		else */
        if (number == 1) {
            // 收到包的前半截数据
            // 也有可能是带标志位的垃圾数据
            d("number == 1 收到包的前半截数据 ");
            return null;
        }
        return packageLenList;
    }

    public synchronized byte[] procRecvPackage(byte[] data, int length) {
        LogUtil.v("buff", "buff: " + BytesUtil.BytestoHexStringPrintf(data, length));
        int ret = writeArray(data, length);
        if (ret == -1) {
            return null;
        }
        return checkFullPackage((byte) ProtocolVh.SYNC_FLAG_HEADER);
    }

    public synchronized List<byte[]> procRecvPackage2(byte[] data, int length) {
        LogUtil.v("buff", "buff2: " + BytesUtil.BytestoHexStringPrintf(data, length));
        int ret = writeArray(data, length);
        if (ret == -1) {
            return null;
        }
        return checkFullPackage2((byte) ProtocolVh.SYNC_FLAG_HEADER);
    }

    boolean autoExpanding = true;

    void Expand(int len) {
        if (!autoExpanding)
            return;
        byte[] new_buf = new byte[capacity + len];
        buffer = new_buf;
    }

    private void d(String msg) {
        LogUtil.w("CircularBuffer", msg);
    }
}
