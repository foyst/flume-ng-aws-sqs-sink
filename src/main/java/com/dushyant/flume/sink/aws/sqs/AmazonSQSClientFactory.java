package com.dushyant.flume.sink.aws.sqs;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dushyant.flume.sink.aws.sqs.ContextUtils.resolve;

public class AmazonSQSClientFactory {

    private final Logger LOG = LoggerFactory.getLogger(AmazonSQSClientFactory.class);

    private final Context context;

    public AmazonSQSClientFactory(Context context) {
        this.context = context;
    }

    public AmazonSQSClient createClient() {

        final String awsAccessKey = resolve(context.getString("awsAccessKey", "env.AWS_ACCESS_KEY"));
        final String awsSecretKey = resolve(context.getString("awsSecretKey", "env.AWS_SECRET_KEY"));
        final String awsSessionToken = resolve(context.getString("awsSecretKey", "env.AWS_SESSION_TOKEN"));
        LOG.debug("awsAccessKey:{}", awsAccessKey);

        if (StringUtils.isBlank(awsAccessKey) || StringUtils.isBlank(awsSecretKey)) {
            LOG.warn("Either awsAccessKey or awsSecretKey not specified. Will use DefaultAWSCredentialsProviderChain " +
                    "to look for AWS credentials.");
            return new AmazonSQSClient();
        }
        else {
            return new AmazonSQSClient(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
        }
    }
}
