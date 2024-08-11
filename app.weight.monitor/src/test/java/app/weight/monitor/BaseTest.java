package app.weight.monitor;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.io.TempDir;

import app.weight.monitor.model.Reading;
import app.weight.monitor.storage.ReadingsLoad;
import app.weight.monitor.storage.ReadingsManager;
import app.weight.monitor.storage.ReadingsStore;
import application.notification.Notification;
import application.notification.NotificationListener;
import application.storage.LoadState;
import application.storage.Storage;
import application.storage.StorageNotificationType;
import application.storage.StoreState;

public class BaseTest {
	public static boolean runMonitor = false;

	@TempDir
	protected File rootDirectory;

	protected Object waitForFinish = new Object();
	protected boolean storeSuccess = false;
	protected boolean loadSuccess = false;
	boolean failedIO = false;

	protected DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

	Reading reading = new Reading(LocalDate.now(), "72.0");

	protected ReadingsLoad readingsLoad = null;
	protected ReadingsStore readingsStore = null;

	protected File modelFile = null;

	protected NotificationListener listener = new NotificationListener() {
		@Override
		public void notify(Notification notification) {
			if (notification.notificationType() instanceof StorageNotificationType) {
				assertTrue(notification.subject().isPresent());
				handleStorage(notification);
			}
		}
	};

	private void handleStorage(Notification notification) {
		StorageNotificationType type = (StorageNotificationType) notification.notificationType();
		switch (type) {
			case Store -> {
				StoreState state = (StoreState) notification.subject().get();
				switch (state) {
					case Complete -> storeData();
					case Failed -> failed();
					case Started -> ignore();
				}
			}
			case Load -> {
				LoadState state = (LoadState) notification.subject().get();
				switch (state) {
					case Complete -> loadData();
					case Failed -> failed();
					case Started -> ignore();
				}
			}
		}
	}

	private void ignore() {
	}

	private void storeData() {
		synchronized (waitForFinish) {
			storeSuccess = true;
			waitForFinish.notifyAll();
		}
	}

	private void loadData() {
		synchronized (waitForFinish) {
			loadSuccess = true;
			waitForFinish.notifyAll();
		}
	}

	private void failed() {
		synchronized (waitForFinish) {
			failedIO = true;
			waitForFinish.notifyAll();
		}
	}

	protected void resetFlags() {
		storeSuccess = false;
		loadSuccess = false;
		failedIO = false;
	}

	protected Storage initStorage() {
		modelFile = new File(rootDirectory, "model.dat");
		readingsStore.setFileName(modelFile.getAbsolutePath());
		Storage storage = new Storage();
		return storage;
	}

	protected void writeToStore(Storage storage) throws InterruptedException {
		synchronized (waitForFinish) {
			storage.storeData(readingsStore);
			waitForFinish.wait();
		}
	}

	protected void addAReading() throws Exception {
		synchronized (waitForFinish) {
			ReadingsManager.instance().addReading(reading);
			waitForFinish.wait();
		}
	}

	protected void addAReading(LocalDate date, String weight) throws Exception {
		reading = new Reading(date, weight);
		synchronized (waitForFinish) {
			ReadingsManager.instance().addReading(reading);
			waitForFinish.wait();
		}
	}

	protected void deleteAReading() throws Exception {
		synchronized (waitForFinish) {
			ReadingsManager.instance().deleteReading(reading);
			waitForFinish.wait();
		}
	}

	protected void clearReadings() throws Exception {
		synchronized (waitForFinish) {
			ReadingsManager.instance().clear();
			waitForFinish.wait();
		}
	}
}
