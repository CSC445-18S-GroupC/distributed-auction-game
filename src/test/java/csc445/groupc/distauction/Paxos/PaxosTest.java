package csc445.groupc.distauction.Paxos;

import csc445.groupc.distauction.Communication.MulticastSimulator;
import csc445.groupc.distauction.Paxos.Messages.Message;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by chris on 4/17/18.
 */
public class PaxosTest {
    @Test
    public void singleProposalThreeNodes() throws InterruptedException {
        final List<CountDownLatch> countDownLatches = new ArrayList<>();
        final List<Paxos<Integer>> paxosList = new ArrayList<>();

        final int v = 5;

        final List<LinkedBlockingQueue<Message>> allSendingQueues = new ArrayList<>();
        final List<LinkedBlockingQueue<Message>> allReceivingQueues = new ArrayList<>();

        final int numNodes = 3;
        for (int i = 0; i < numNodes; i++) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final Paxos<Integer> paxos = new Paxos<>(i, 3, (a) -> {
                if (a == v) {
                    countDownLatch.countDown();
                }
            }, Optional.empty());

            countDownLatches.add(countDownLatch);
            paxosList.add(paxos);

            paxos.run();

            allSendingQueues.add(paxos.getSendingQueue());
            allReceivingQueues.add(paxos.getReceivingQueue());
        }

        final MulticastSimulator multicastSimulator = new MulticastSimulator(allSendingQueues, allReceivingQueues);

        onThread(() -> {
            try {multicastSimulator.run();} catch (Exception e) {}
        });

        paxosList.get(0).proposeStep(v);

        try {
            for (int i = 0; i < numNodes; i++) {
                countDownLatches.get(i).await(2, TimeUnit.SECONDS);
                assertEquals(0, countDownLatches.get(i).getCount());
            }
        } catch (InterruptedException e) {
            Assert.fail();
        }
    }

    @Test
    public void singleProposalThreeNodesOneDown() throws InterruptedException {
        final List<CountDownLatch> countDownLatches = new ArrayList<>();
        final List<Paxos<Integer>> paxosList = new ArrayList<>();

        final int v = 5;

        final List<LinkedBlockingQueue<Message>> allSendingQueues = new ArrayList<>();
        final List<LinkedBlockingQueue<Message>> allReceivingQueues = new ArrayList<>();

        final int numNodes = 3;
        for (int i = 0; i < numNodes; i++) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final Paxos<Integer> paxos = new Paxos<>(i, 3, (a) -> {
                if (a == v) {
                    countDownLatch.countDown();
                }
            }, Optional.empty());

            countDownLatches.add(countDownLatch);
            paxosList.add(paxos);

            if (i != numNodes - 1) {
                paxos.run();
            }

            allSendingQueues.add(paxos.getSendingQueue());
            allReceivingQueues.add(paxos.getReceivingQueue());
        }

        final MulticastSimulator multicastSimulator = new MulticastSimulator(allSendingQueues, allReceivingQueues);

        onThread(() -> {
            try {multicastSimulator.run();} catch (Exception e) {}
        });

        paxosList.get(0).proposeStep(v);

        try {
            for (int i = 0; i < numNodes - 1; i++) {
                countDownLatches.get(i).await(2, TimeUnit.SECONDS);
                assertEquals(0, countDownLatches.get(i).getCount());
            }
            assertEquals(1, countDownLatches.get(numNodes - 1).getCount());
        } catch (InterruptedException e) {
            Assert.fail();
        }
    }

    @Test
    public void singleProposalThreeNodesUpdating() throws InterruptedException {
        final List<CountDownLatch> countDownLatches = new ArrayList<>();
        final List<Paxos<Integer>> paxosList = new ArrayList<>();

        final int v = 5;

        final List<LinkedBlockingQueue<Message>> allSendingQueues = new ArrayList<>();
        final List<LinkedBlockingQueue<Message>> allReceivingQueues = new ArrayList<>();

        final int numNodes = 3;
        for (int i = 0; i < numNodes; i++) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final Paxos<Integer> paxos = new Paxos<>(i, 3, (a) -> {
                if (a == v) {
                    countDownLatch.countDown();
                }
            }, Optional.empty());

            countDownLatches.add(countDownLatch);
            paxosList.add(paxos);

            if (i != numNodes - 1) {
                paxos.run();
            }

            allSendingQueues.add(paxos.getSendingQueue());
            allReceivingQueues.add(paxos.getReceivingQueue());
        }

        final MulticastSimulator multicastSimulator = new MulticastSimulator(allSendingQueues, allReceivingQueues);

        onThread(() -> {
            try {multicastSimulator.run();} catch (Exception e) {}
        });

        paxosList.get(0).proposeStep(v);

        try {
            for (int i = 0; i < numNodes - 1; i++) {
                countDownLatches.get(i).await(2, TimeUnit.SECONDS);
                assertEquals(0, countDownLatches.get(i).getCount());
            }
            assertEquals(1, countDownLatches.get(numNodes - 1).getCount());

            final LinkedBlockingQueue<Message> queue = paxosList.get(numNodes - 1).getReceivingQueue();
            while (!queue.isEmpty()) {
                queue.poll();
            }

            paxosList.get(numNodes - 1).run();
            Thread.sleep(2000);
            paxosList.get(numNodes - 1).proposeStep(v);

            countDownLatches.get(numNodes - 1).await(2, TimeUnit.SECONDS);
            assertEquals(0, countDownLatches.get(numNodes - 1).getCount());
        } catch (InterruptedException e) {
            Assert.fail();
        }
    }

    private static void onThread(final Runnable runnable) {
        new Thread(runnable).start();
    }
}
