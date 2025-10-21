terraform {
  backend "s3" {
    bucket         = "terraform-state-XXXXXXXXXXX"
    dynamodb_table = "terraform_state"
    key            = "domain-lambda-backend.tfstate"
    region         = "ap-southeast-1"
    assume_role = {
      role_arn = "arn:aws:iam::XXXXXXXXXXX:role/Access-Terraform-State"
    }
  }
}

provider "aws" {
  region = local.region

  assume_role {
    role_arn = "arn:aws:iam::${var.account_id}:role/deploy-app"
  }
}

# Data sources
data "archive_file" "lambda_zip" {
  type        = "zip"
  source_dir  = "${path.module}/dist"
  output_path = "${path.module}/function-native-zip.zip"
}

# DynamoDB Table
resource "aws_dynamodb_table" "products_table" {
  name           = "${var.app_name}-products"
  billing_mode   = "PAY_PER_REQUEST"
  hash_key       = "PK"

  attribute {
    name = "PK"
    type = "S"
  }

  tags = {
    Name        = "${var.app_name}-products-table"
    Environment = var.environment
  }
}

# API Gateway
resource "aws_api_gateway_rest_api" "api" {
  name        = "My Lambda springboot GraalVM Sample API"
  description = "Sample API for lambda-springboot"

  tags = {
    Name        = "${var.app_name}-api"
    Environment = var.environment
  }
}

resource "aws_api_gateway_deployment" "api_deployment" {
  depends_on = [
    aws_api_gateway_method.get_products,
    aws_api_gateway_method.get_product_by_id,
    aws_api_gateway_method.put_product,
    aws_api_gateway_method.delete_product,
    aws_api_gateway_method.delete_account,
  ]

  rest_api_id = aws_api_gateway_rest_api.api.id
}

resource "aws_api_gateway_stage" "api_deployment" {
  deployment_id = aws_api_gateway_deployment.api_deployment.id
  rest_api_id   = aws_api_gateway_rest_api.api.id
  stage_name    = "prod"
}

# Products resource
resource "aws_api_gateway_resource" "products" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_rest_api.api.root_resource_id
  path_part   = "products"
}

# Products/{id} resource
resource "aws_api_gateway_resource" "products_id" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_resource.products.id
  path_part   = "{id}"
}

# Account resource
resource "aws_api_gateway_resource" "account" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_rest_api.api.root_resource_id
  path_part   = "account"
}

# Account/delete resource
resource "aws_api_gateway_resource" "account_delete" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_resource.account.id
  path_part   = "delete"
}

# GET /products
resource "aws_api_gateway_method" "get_products" {
  rest_api_id   = aws_api_gateway_rest_api.api.id
  resource_id   = aws_api_gateway_resource.products.id
  http_method   = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "get_products_integration" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_resource.products.id
  http_method = aws_api_gateway_method.get_products.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.get_products.invoke_arn
}

# GET /products/{id}
resource "aws_api_gateway_method" "get_product_by_id" {
  rest_api_id   = aws_api_gateway_rest_api.api.id
  resource_id   = aws_api_gateway_resource.products_id.id
  http_method   = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "get_product_by_id_integration" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_resource.products_id.id
  http_method = aws_api_gateway_method.get_product_by_id.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.get_product_by_id.invoke_arn
}

# PUT /products/{id}
resource "aws_api_gateway_method" "put_product" {
  rest_api_id   = aws_api_gateway_rest_api.api.id
  resource_id   = aws_api_gateway_resource.products_id.id
  http_method   = "PUT"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "put_product_integration" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_resource.products_id.id
  http_method = aws_api_gateway_method.put_product.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.put_product.invoke_arn
}

# DELETE /products/{id}
resource "aws_api_gateway_method" "delete_product" {
  rest_api_id   = aws_api_gateway_rest_api.api.id
  resource_id   = aws_api_gateway_resource.products_id.id
  http_method   = "DELETE"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "delete_product_integration" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_resource.products_id.id
  http_method = aws_api_gateway_method.delete_product.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.delete_product.invoke_arn
}

# POST /account/delete
resource "aws_api_gateway_method" "delete_account" {
  rest_api_id   = aws_api_gateway_rest_api.api.id
  resource_id   = aws_api_gateway_resource.account_delete.id
  http_method   = "POST"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "delete_account_integration" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_resource.account_delete.id
  http_method = aws_api_gateway_method.delete_account.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.delete_account.invoke_arn
}

# IAM Role for Lambda
resource "aws_iam_role" "lambda_execution_role" {
  name = "${var.app_name}-lambda-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_basic_execution" {
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
  role       = aws_iam_role.lambda_execution_role.name
}

resource "aws_iam_role_policy_attachment" "lambda_xray_execution" {
  policy_arn = "arn:aws:iam::aws:policy/AWSXRayDaemonWriteAccess"
  role       = aws_iam_role.lambda_execution_role.name
}

# Lambda permissions for API Gateway
resource "aws_lambda_permission" "api_gateway_get_products" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.get_products.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.api.execution_arn}/*/*"
}

resource "aws_lambda_permission" "api_gateway_get_product_by_id" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.get_product_by_id.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.api.execution_arn}/*/*"
}

resource "aws_lambda_permission" "api_gateway_put_product" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.put_product.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.api.execution_arn}/*/*"
}

resource "aws_lambda_permission" "api_gateway_delete_product" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.delete_product.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.api.execution_arn}/*/*"
}

resource "aws_lambda_permission" "api_gateway_delete_account" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.delete_account.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.api.execution_arn}/*/*"
}

# Outputs
output "api_endpoint" {
  description = "API Gateway endpoint URL for Prod stage for Springboot sample function"
  value       = "https://${aws_api_gateway_rest_api.api.id}.execute-api.${local.region}.amazonaws.com/prod/products"
}

output "delete_account_endpoint" {
  description = "API Gateway endpoint URL for delete account functionality"
  value       = "https://${aws_api_gateway_rest_api.api.id}.execute-api.${local.region}.amazonaws.com/prod/account/delete"
}