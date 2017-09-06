package com.dushyant.flume.sink.aws.sqs;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.STSSessionCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dushyant.flume.sink.aws.sqs.ContextUtils.resolve;

public class AmazonSQSClientFactory {

    private final Logger LOG = LoggerFactory.getLogger(AmazonSQSClientFactory.class);

    private final String awsAccessKey;
    private final String awsSecretKey;
    private final String awsSessionToken;

    public AmazonSQSClientFactory(String awsAccessKey, String awsSecretKey, String awsSessionToken) {
        this.awsAccessKey = awsAccessKey;
        this.awsSecretKey = awsSecretKey;
        this.awsSessionToken = awsSessionToken;
    }

    public AmazonSQSClient createClient() {

        if (temporaryCredentialsProvided(awsSessionToken, awsAccessKey, awsSecretKey)) {
            return new AmazonSQSClient(new BasicSessionCredentials(awsAccessKey, awsSecretKey, awsSessionToken));
        } else if (standardCredentialsProvided(awsAccessKey, awsSecretKey)) {
            return new AmazonSQSClient(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
        } else {
            LOG.info("Either awsAccessKey or awsSecretKey not specified. Will use DefaultAWSCredentialsProviderChain " +
                    "to look for AWS credentials.");
            return new AmazonSQSClient();
        }
    }

    private boolean standardCredentialsProvided(String awsAccessKey, String awsSecretKey) {
        return !StringUtils.isBlank(awsAccessKey) && !StringUtils.isBlank(awsSecretKey);
    }

    private boolean temporaryCredentialsProvided(String awsSessionToken, String awsAccessKey, String awsSecretKey) {
        return !StringUtils.isBlank(awsSessionToken) && standardCredentialsProvided(awsAccessKey, awsSecretKey);
    }
}
